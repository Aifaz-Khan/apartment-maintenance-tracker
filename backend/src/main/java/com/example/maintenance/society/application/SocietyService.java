package com.example.maintenance.society.application;

import com.example.maintenance.society.api.*;
import com.example.maintenance.society.domain.*;
import com.example.maintenance.society.infrastructure.*;
import com.example.maintenance.user.domain.*;
import com.example.maintenance.user.infrastructure.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SocietyService {
    private final SocietyRepository societies;
    private final SocietyAdminRepository admins;
    private final UserRepository users;
    private final PasswordEncoder passwords;

    public SocietyService(SocietyRepository societies, SocietyAdminRepository admins, UserRepository users, PasswordEncoder passwords) {
        this.societies=societies; this.admins=admins; this.users=users; this.passwords=passwords;
    }

    @Transactional public SocietyResponse create(SocietyCreateRequest request) {
        if (societies.existsByCodeIgnoreCase(request.code().trim())) throw new ResourceConflictException("A society with this code already exists.");
        Society society = new Society(request.name().trim(), request.code().trim(), request.addressLine1().trim(), clean(request.addressLine2()), request.city().trim(), request.state().trim(), request.postalCode().trim(), request.contactEmail().trim().toLowerCase());
        return SocietyResponse.from(societies.save(society));
    }

    @Transactional(readOnly=true) public PageResponse<SocietyResponse> list(int page, int size) {
        Page<Society> result=societies.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.ASC,"name")));
        return PageResponse.from(result, SocietyResponse::from);
    }

    @Transactional(readOnly=true) public SocietyResponse get(Long id) { return SocietyResponse.from(findSociety(id)); }

    @Transactional public SocietyResponse update(Long id, SocietyUpdateRequest request) {
        Society society=findSociety(id);
        if (request.name()!=null) society.setName(required(request.name(), "name"));
        if (request.code()!=null) {
            String code=required(request.code(), "code");
            if (!society.getCode().equalsIgnoreCase(code) && societies.existsByCodeIgnoreCase(code)) throw new ResourceConflictException("A society with this code already exists.");
            society.setCode(code);
        }
        if (request.addressLine1()!=null) society.setAddressLine1(required(request.addressLine1(), "addressLine1"));
        if (request.addressLine2()!=null) society.setAddressLine2(clean(request.addressLine2()));
        if (request.city()!=null) society.setCity(required(request.city(), "city"));
        if (request.state()!=null) society.setState(required(request.state(), "state"));
        if (request.postalCode()!=null) society.setPostalCode(required(request.postalCode(), "postalCode"));
        if (request.contactEmail()!=null) society.setContactEmail(required(request.contactEmail(), "contactEmail").toLowerCase());
        if (request.status()!=null) society.setStatus(request.status().name());
        return SocietyResponse.from(society);
    }

    @Transactional public SocietyAdminResponse appointAdmin(Long societyId, SocietyAdminAppointmentRequest request) {
        Society society=findSociety(societyId);
        String email=request.email().trim().toLowerCase();
        if (users.existsByEmailIgnoreCase(email)) throw new ResourceConflictException("An account with this email already exists.");
        User admin=users.save(new User(email,passwords.encode(request.initialPassword()),request.fullName().trim(),clean(request.phone()),UserRole.SOCIETY_ADMIN));
        admins.save(new SocietyAdmin(admin,society));
        return new SocietyAdminResponse(admin.getId(),admin.getEmail(),admin.getFullName(),society.getId(),society.getName());
    }

    private Society findSociety(Long id) { return societies.findById(id).orElseThrow(()->new ResourceNotFoundException("Society not found.")); }
    private String required(String value, String field) { if (value.isBlank()) throw new IllegalArgumentException(field + " must not be blank."); return value.trim(); }
    private String clean(String value) { return value==null || value.isBlank()?null:value.trim(); }
}
