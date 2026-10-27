package com.example.maintenance.residency.application;

import com.example.maintenance.apartment.infrastructure.ApartmentUnitRepository;
import com.example.maintenance.residency.api.*;
import com.example.maintenance.residency.domain.*;
import com.example.maintenance.residency.infrastructure.UnitResidencyRepository;
import com.example.maintenance.society.application.*;
import com.example.maintenance.society.infrastructure.SocietyAdminRepository;
import com.example.maintenance.society.domain.SocietyAdmin;
import com.example.maintenance.user.domain.*;
import com.example.maintenance.user.infrastructure.UserRepository;
import com.example.maintenance.society.api.PageResponse;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;

@Service
public class ResidentRegistrationService {
    private final ApartmentUnitRepository units;private final UnitResidencyRepository residencies;private final UserRepository users;
    private final SocietyAdminRepository admins;private final SocietyAccessService access;private final PasswordEncoder passwords;
    public ResidentRegistrationService(ApartmentUnitRepository units,UnitResidencyRepository residencies,UserRepository users,SocietyAdminRepository admins,SocietyAccessService access,PasswordEncoder passwords){this.units=units;this.residencies=residencies;this.users=users;this.admins=admins;this.access=access;this.passwords=passwords;}

    @Transactional public ResidencyRequestResponse submit(ResidentRegistrationRequest request){
        var unit=units.findById(request.apartmentUnitId()).orElseThrow(()->new ResourceNotFoundException("Society or unit not found."));
        if(!unit.getSociety().getId().equals(request.societyId())||!"ACTIVE".equals(unit.getSociety().getStatus()))throw new ResourceNotFoundException("Society or unit not found.");
        String email=request.email().trim().toLowerCase();
        if(users.existsByEmailIgnoreCase(email))throw new ResourceConflictException("An account with this email already exists.");
        User resident=new User(email,passwords.encode(request.password()),request.fullName().trim(),clean(request.phone()),UserRole.RESIDENT);resident.setActive(false);
        resident=users.save(resident);
        UnitResidency residency=new UnitResidency(unit,resident,request.residencyType(),LocalDate.now(),ResidencyStatus.PENDING);
        return ResidencyRequestResponse.from(residencies.save(residency));
    }

    @Transactional(readOnly=true) public PageResponse<ResidencyRequestResponse> pending(Long adminId,int page,int size){
        Long societyId=access.requireAssignedSociety(adminId);validatePage(page,size);
        Page<UnitResidency> result=residencies.findAllByApartmentUnitSocietyIdAndStatus(societyId,ResidencyStatus.PENDING,PageRequest.of(page,size,Sort.by(Sort.Direction.ASC,"createdAt")));
        return PageResponse.from(result,ResidencyRequestResponse::from);
    }

    @Transactional public ResidencyRequestResponse review(Long adminId,Long requestId,ResidentRequestReview review){
        UnitResidency residency=residencies.findById(requestId).orElseThrow(()->new ResourceNotFoundException("Resident request not found."));
        access.requireSocietyScope(adminId,residency.getApartmentUnit().getSociety().getId());
        if(residency.getStatus()!=ResidencyStatus.PENDING)throw new ResourceConflictException("This resident request has already been reviewed.");
        if(review.action()==ResidentRequestReview.Action.APPROVE){
            if(residencies.existsByApartmentUnitIdAndStatus(residency.getApartmentUnit().getId(),ResidencyStatus.ACTIVE))throw new ResourceConflictException("This unit already has an active residency.");
            SocietyAdmin admin=admins.findByUserId(adminId).orElseThrow(()->new ResourceNotFoundException("Society Admin assignment not found."));
            residency.setStatus(ResidencyStatus.ACTIVE);residency.setVerifiedBy(admin.getUser());residency.setVerifiedAt(Instant.now());residency.getResidentUser().setActive(true);
        } else {
            if(review.rejectionReason()==null||review.rejectionReason().isBlank())throw new IllegalArgumentException("A rejection reason is required.");
            residency.setStatus(ResidencyStatus.REJECTED);residency.setEndDate(LocalDate.now());residency.setRejectionReason(review.rejectionReason().trim());
        }
        return ResidencyRequestResponse.from(residency);
    }
    private void validatePage(int page,int size){if(page<0||size<1||size>100)throw new IllegalArgumentException("Page must be non-negative and size must be between 1 and 100.");}
    private String clean(String value){return value==null||value.isBlank()?null:value.trim();}
}
