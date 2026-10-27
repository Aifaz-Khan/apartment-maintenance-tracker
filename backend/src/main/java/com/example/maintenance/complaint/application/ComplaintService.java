package com.example.maintenance.complaint.application;

import com.example.maintenance.apartment.domain.ApartmentUnit;
import com.example.maintenance.category.domain.ComplaintCategory;
import com.example.maintenance.category.infrastructure.CategoryRepository;
import com.example.maintenance.complaint.api.*;
import com.example.maintenance.complaint.domain.*;
import com.example.maintenance.complaint.infrastructure.*;
import com.example.maintenance.residency.domain.*;
import com.example.maintenance.residency.infrastructure.UnitResidencyRepository;
import com.example.maintenance.society.api.PageResponse;
import com.example.maintenance.society.application.*;
import com.example.maintenance.user.domain.*;
import com.example.maintenance.user.infrastructure.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.*;

@Service
public class ComplaintService {
    private static final EnumSet<ComplaintStatus> ACTIVE_STATUSES=EnumSet.of(
        ComplaintStatus.SUBMITTED,ComplaintStatus.ACKNOWLEDGED,ComplaintStatus.IN_PROGRESS,
        ComplaintStatus.ON_HOLD,ComplaintStatus.RESOLVED,ComplaintStatus.REOPENED);
    private static final Map<ComplaintStatus,Set<ComplaintStatus>> TRANSITIONS=Map.of(
        ComplaintStatus.SUBMITTED,EnumSet.of(ComplaintStatus.ACKNOWLEDGED,ComplaintStatus.CANCELLED),
        ComplaintStatus.ACKNOWLEDGED,EnumSet.of(ComplaintStatus.IN_PROGRESS,ComplaintStatus.ON_HOLD,ComplaintStatus.CANCELLED),
        ComplaintStatus.IN_PROGRESS,EnumSet.of(ComplaintStatus.ON_HOLD,ComplaintStatus.RESOLVED,ComplaintStatus.CANCELLED),
        ComplaintStatus.ON_HOLD,EnumSet.of(ComplaintStatus.IN_PROGRESS,ComplaintStatus.RESOLVED,ComplaintStatus.CANCELLED),
        ComplaintStatus.RESOLVED,EnumSet.of(ComplaintStatus.CLOSED),
        ComplaintStatus.REOPENED,EnumSet.of(ComplaintStatus.ACKNOWLEDGED,ComplaintStatus.IN_PROGRESS,ComplaintStatus.ON_HOLD,ComplaintStatus.RESOLVED),
        ComplaintStatus.CLOSED,EnumSet.noneOf(ComplaintStatus.class),
        ComplaintStatus.CANCELLED,EnumSet.noneOf(ComplaintStatus.class));

    private final UnitResidencyRepository residencies;
    private final UserRepository users;
    private final CategoryRepository categories;
    private final ComplaintRepository complaints;
    private final ComplaintStatusHistoryRepository history;
    private final SocietyAccessService societyAccess;

    public ComplaintService(UnitResidencyRepository residencies,UserRepository users,CategoryRepository categories,
        ComplaintRepository complaints,ComplaintStatusHistoryRepository history,SocietyAccessService societyAccess){
        this.residencies=residencies;this.users=users;this.categories=categories;this.complaints=complaints;
        this.history=history;this.societyAccess=societyAccess;
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
        try {
            complaint=complaints.saveAndFlush(complaint);
        } catch(org.springframework.dao.DataIntegrityViolationException ex) {
            if(isActiveComplaintConstraint(ex))throw new ActiveComplaintExistsException();
            throw ex;
        }
        history.save(new ComplaintStatusHistory(complaint,resident,null,ComplaintStatus.SUBMITTED,"Complaint submitted."));
        return ComplaintResponse.from(complaint);
    }

    @Transactional(readOnly=true) public PageResponse<ComplaintResponse> list(Long requesterId,int page,int size){
        validatePage(page,size);User requester=activeUser(requesterId);Page<Complaint> result;
        if(requester.getRole()==UserRole.SOCIETY_ADMIN){
            Long societyId=societyAccess.requireAssignedSociety(requesterId);
            result=complaints.findAllBySocietyId(societyId,PageRequest.of(page,size,Sort.by(Sort.Direction.DESC,"createdAt")));
        } else if(requester.getRole()==UserRole.RESIDENT){
            UnitResidency residency=activeResidency(requesterId);
            result=complaints.findAllByApartmentUnitId(residency.getApartmentUnit().getId(),PageRequest.of(page,size,Sort.by(Sort.Direction.DESC,"createdAt")));
        } else throw new AccessDeniedException("This account cannot view resident complaints.");
        return PageResponse.from(result,ComplaintResponse::from);
    }

    @Transactional(readOnly=true) public ComplaintResponse get(Long requesterId,Long complaintId){
        Complaint complaint=findComplaint(complaintId);authorizeView(activeUser(requesterId),complaint);return ComplaintResponse.from(complaint);
    }

    @Transactional(readOnly=true) public List<ComplaintStatusHistoryResponse> getHistory(Long requesterId,Long complaintId){
        Complaint complaint=findComplaint(complaintId);authorizeView(activeUser(requesterId),complaint);
        return history.findAllByComplaintIdOrderByCreatedAtAsc(complaintId).stream().map(ComplaintStatusHistoryResponse::from).toList();
    }

    @Transactional public ComplaintResponse updateStatus(Long adminId,Long complaintId,ComplaintStatusUpdateRequest request){
        User admin=activeUser(adminId);
        if(admin.getRole()!=UserRole.SOCIETY_ADMIN)throw new AccessDeniedException("Only a Society Admin can update complaint status.");
        Complaint complaint=findComplaint(complaintId);
        societyAccess.requireSocietyScope(adminId,complaint.getSociety().getId());
        ComplaintStatus oldStatus=complaint.getStatus(),newStatus=request.newStatus();
        if(!TRANSITIONS.get(oldStatus).contains(newStatus))throw new InvalidStatusTransitionException("Cannot change complaint status from "+oldStatus+" to "+newStatus+".");
        if(newStatus==ComplaintStatus.RESOLVED&&(request.resolutionNotes()==null||request.resolutionNotes().isBlank()))throw new IllegalArgumentException("Resolution notes are required when resolving a complaint.");
        complaint.setStatus(newStatus);
        if(request.resolutionNotes()!=null&&!request.resolutionNotes().isBlank())complaint.setResolutionNotes(request.resolutionNotes().trim());
        if(newStatus==ComplaintStatus.RESOLVED)complaint.setResolvedAt(Instant.now());
        if(newStatus==ComplaintStatus.CLOSED)complaint.setClosedAt(Instant.now());
        if(newStatus==ComplaintStatus.REOPENED){complaint.setResolvedAt(null);complaint.setClosedAt(null);}
        history.save(new ComplaintStatusHistory(complaint,admin,oldStatus,newStatus,clean(request.remarks())));
        return ComplaintResponse.from(complaint);
    }

    private User activeUser(Long id){return users.findById(id).filter(User::isActive).orElseThrow(()->new ResourceNotFoundException("Active user account not found."));}
    private UnitResidency activeResidency(Long userId){return residencies.findByResidentUserIdAndStatus(userId,ResidencyStatus.ACTIVE).orElseThrow(()->new AccessDeniedException("An active apartment residency is required."));}
    private Complaint findComplaint(Long id){return complaints.findById(id).orElseThrow(()->new ResourceNotFoundException("Complaint not found."));}
    private void authorizeView(User requester,Complaint complaint){
        if(requester.getRole()==UserRole.SOCIETY_ADMIN){societyAccess.requireSocietyScope(requester.getId(),complaint.getSociety().getId());return;}
        if(requester.getRole()==UserRole.RESIDENT){UnitResidency residency=activeResidency(requester.getId());if(!residency.getApartmentUnit().getId().equals(complaint.getApartmentUnit().getId()))throw new AccessDeniedException("This complaint belongs to another apartment.");return;}
        throw new AccessDeniedException("This account cannot view resident complaints.");
    }
    private void validatePage(int page,int size){if(page<0||size<1||size>100)throw new IllegalArgumentException("Page must be non-negative and size must be between 1 and 100.");}
    private boolean isActiveComplaintConstraint(Throwable error){for(Throwable cause=error;cause!=null;cause=cause.getCause())if(cause.getMessage()!=null&&cause.getMessage().contains("uq_one_active_complaint_per_apartment"))return true;return false;}
    private String clean(String value){return value==null||value.isBlank()?null:value.trim();}
}
