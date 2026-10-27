package com.example.maintenance.complaint.application;

import com.example.maintenance.apartment.domain.ApartmentUnit;
import com.example.maintenance.category.domain.ComplaintCategory;
import com.example.maintenance.category.infrastructure.CategoryRepository;
import com.example.maintenance.complaint.api.*;
import com.example.maintenance.complaint.domain.*;
import com.example.maintenance.complaint.infrastructure.ComplaintRepository;
import com.example.maintenance.residency.domain.*;
import com.example.maintenance.residency.infrastructure.UnitResidencyRepository;
import com.example.maintenance.society.application.ResourceConflictException;
import com.example.maintenance.society.application.ResourceNotFoundException;
import com.example.maintenance.user.domain.*;
import com.example.maintenance.user.infrastructure.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.EnumSet;

@Service
public class ComplaintService {
    private static final EnumSet<ComplaintStatus> ACTIVE_STATUSES=EnumSet.of(
        ComplaintStatus.SUBMITTED,ComplaintStatus.ACKNOWLEDGED,ComplaintStatus.IN_PROGRESS,
        ComplaintStatus.ON_HOLD,ComplaintStatus.RESOLVED,ComplaintStatus.REOPENED);
    private final UnitResidencyRepository residencies;
    private final UserRepository users;
    private final CategoryRepository categories;
    private final ComplaintRepository complaints;

    public ComplaintService(UnitResidencyRepository residencies,UserRepository users,CategoryRepository categories,ComplaintRepository complaints){
        this.residencies=residencies;this.users=users;this.categories=categories;this.complaints=complaints;
    }

    @Transactional public ComplaintResponse create(Long residentId,ComplaintCreateRequest request){
        User resident=users.findById(residentId).filter(User::isActive)
            .filter(user->user.getRole()==UserRole.RESIDENT)
            .orElseThrow(()->new ResourceNotFoundException("Active resident account not found."));
        UnitResidency residency=residencies.findByResidentUserIdAndStatus(residentId,ResidencyStatus.ACTIVE)
            .orElseThrow(()->new ResourceConflictException("An approved active residency is required to submit a complaint."));
        ApartmentUnit unit=residency.getApartmentUnit();
        ComplaintCategory category=categories.findById(request.categoryId())
            .filter(ComplaintCategory::isActive)
            .filter(candidate->candidate.getSociety()==null||candidate.getSociety().getId().equals(unit.getSociety().getId()))
            .orElseThrow(()->new ResourceNotFoundException("Active complaint category not found."));
        if(complaints.existsByApartmentUnitIdAndStatusIn(unit.getId(),ACTIVE_STATUSES))throw new ActiveComplaintExistsException();
        Complaint complaint=new Complaint(unit.getSociety(),unit,category,resident,request.title().trim(),request.description().trim(),
            request.locationArea().trim(),clean(request.locationDetails()),request.priority());
        try { return ComplaintResponse.from(complaints.saveAndFlush(complaint)); }
        catch(org.springframework.dao.DataIntegrityViolationException ex) {
            if(isActiveComplaintConstraint(ex))throw new ActiveComplaintExistsException();
            throw ex;
        }
    }

    private boolean isActiveComplaintConstraint(Throwable error){
        for(Throwable cause=error;cause!=null;cause=cause.getCause())if(cause.getMessage()!=null&&cause.getMessage().contains("uq_one_active_complaint_per_apartment"))return true;
        return false;
    }
    private String clean(String value){return value==null||value.isBlank()?null:value.trim();}
}
