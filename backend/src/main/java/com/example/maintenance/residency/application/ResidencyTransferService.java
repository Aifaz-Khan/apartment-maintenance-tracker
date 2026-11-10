package com.example.maintenance.residency.application;

import com.example.maintenance.apartment.api.ApartmentUnitResponse;
import com.example.maintenance.apartment.domain.ApartmentUnit;
import com.example.maintenance.apartment.infrastructure.ApartmentUnitRepository;
import com.example.maintenance.residency.api.*;
import com.example.maintenance.residency.domain.*;
import com.example.maintenance.residency.infrastructure.*;
import com.example.maintenance.society.api.PageResponse;
import com.example.maintenance.society.application.*;
import com.example.maintenance.society.domain.Society;
import com.example.maintenance.user.domain.*;
import com.example.maintenance.user.infrastructure.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.LocalDate;

@Service
public class ResidencyTransferService {
    private final ApartmentUnitRepository units;
    private final UnitResidencyRepository residencies;
    private final ResidencyTransferRepository transfers;
    private final UserRepository users;
    private final SocietyAccessService societyAccess;

    public ResidencyTransferService(ApartmentUnitRepository units,UnitResidencyRepository residencies,
        ResidencyTransferRepository transfers,UserRepository users,SocietyAccessService societyAccess){
        this.units=units;this.residencies=residencies;this.transfers=transfers;this.users=users;this.societyAccess=societyAccess;
    }

    @Transactional
    public ResidencyTransferResponse submit(Long residentId,ResidencyTransferCreateRequest request){
        User resident=activeUser(residentId);
        if(resident.getRole()!=UserRole.RESIDENT)throw new AccessDeniedException("Only a resident can request a unit transfer.");
        UnitResidency current=residencies.findByResidentUserIdAndStatus(residentId,ResidencyStatus.ACTIVE)
            .orElseThrow(()->new ResourceConflictException("An active residency is required to request a transfer."));
        ApartmentUnit target=units.findById(request.targetApartmentUnitId())
            .orElseThrow(()->new ResourceNotFoundException("Target apartment unit not found."));
        Society society=current.getApartmentUnit().getSociety();
        if(!target.getSociety().getId().equals(society.getId()))throw new ResourceNotFoundException("Target apartment unit not found.");
        if(!"ACTIVE".equals(society.getStatus()))throw new ResourceConflictException("Transfers are unavailable for an inactive society.");
        if(target.getId().equals(current.getApartmentUnit().getId()))throw new ResourceConflictException("Choose a different target unit.");
        if(residencies.existsByApartmentUnitIdAndStatus(target.getId(),ResidencyStatus.ACTIVE))throw new ResourceConflictException("The target unit already has an active resident.");
        if(transfers.existsByProposedUserIdAndStatus(residentId,TransferStatus.PENDING))throw new ResourceConflictException("You already have a transfer request waiting for review.");
        if(transfers.existsByApartmentUnitIdAndStatus(target.getId(),TransferStatus.PENDING))throw new ResourceConflictException("The target unit already has a transfer request waiting for review.");
        ResidencyTransferRequest transfer=new ResidencyTransferRequest(target,current,resident,current.getResidencyType(),resident,
            request.effectiveDate(),request.reason().trim());
        try{return ResidencyTransferResponse.from(transfers.saveAndFlush(transfer));}
        catch(DataIntegrityViolationException ex){throw new ResourceConflictException("A transfer request for this resident or unit is already pending.");}
    }

    @Transactional(readOnly=true)
    public PageResponse<ResidencyTransferResponse> mine(Long residentId,int page,int size){
        User resident=activeUser(residentId);
        if(resident.getRole()!=UserRole.RESIDENT)throw new AccessDeniedException("Only a resident can view their transfer requests.");
        validatePage(page,size);
        return PageResponse.from(transfers.findAllByProposedUserIdOrderByCreatedAtDesc(residentId,pageable(page,size)),ResidencyTransferResponse::from);
    }

    @Transactional(readOnly=true)
    public PageResponse<ApartmentUnitResponse> availableUnits(Long residentId,int page,int size){
        User resident=activeUser(residentId);
        if(resident.getRole()!=UserRole.RESIDENT)throw new AccessDeniedException("Only a resident can request available transfer units.");
        validatePage(page,size);
        UnitResidency current=residencies.findByResidentUserIdAndStatus(residentId,ResidencyStatus.ACTIVE)
            .orElseThrow(()->new ResourceConflictException("An active residency is required to request a transfer."));
        Long societyId=current.getApartmentUnit().getSociety().getId();
        Page<ApartmentUnit> result=units.findAvailableForTransfer(societyId,current.getApartmentUnit().getId(),
            ResidencyStatus.ACTIVE,TransferStatus.PENDING,pageable(page,size));
        return PageResponse.from(result,ApartmentUnitResponse::from);
    }

    @Transactional(readOnly=true)
    public PageResponse<ResidencyTransferResponse> pending(Long adminId,int page,int size){
        User admin=activeUser(adminId);
        if(admin.getRole()!=UserRole.SOCIETY_ADMIN)throw new AccessDeniedException("Only a Society Admin can review transfer requests.");
        Long societyId=societyAccess.requireAssignedSociety(adminId);validatePage(page,size);
        Page<ResidencyTransferRequest> result=transfers.findAllByApartmentUnitSocietyIdAndStatusOrderByCreatedAtAsc(
            societyId,TransferStatus.PENDING,pageable(page,size));
        return PageResponse.from(result,ResidencyTransferResponse::from);
    }

    @Transactional
    public ResidencyTransferResponse review(Long adminId,Long transferId,ResidencyTransferReviewRequest request){
        User admin=activeUser(adminId);
        if(admin.getRole()!=UserRole.SOCIETY_ADMIN)throw new AccessDeniedException("Only a Society Admin can review transfer requests.");
        ResidencyTransferRequest transfer=transfers.findByIdForUpdate(transferId)
            .orElseThrow(()->new ResourceNotFoundException("Transfer request not found."));
        societyAccess.requireSocietyScope(adminId,transfer.getApartmentUnit().getSociety().getId());
        if(transfer.getStatus()!=TransferStatus.PENDING)throw new ResourceConflictException("This transfer request has already been reviewed.");
        transfer.setReviewedBy(admin);transfer.setReviewedAt(Instant.now());
        if(request.action()==ResidencyTransferReviewRequest.Action.REJECT){
            if(request.rejectionReason()==null||request.rejectionReason().isBlank())throw new IllegalArgumentException("A rejection reason is required.");
            transfer.setRejectionReason(request.rejectionReason().trim());transfer.setStatus(TransferStatus.REJECTED);
            return ResidencyTransferResponse.from(transfer);
        }

        LocalDate today=LocalDate.now();
        if(transfer.getEffectiveDate().isAfter(today))throw new ResourceConflictException("This transfer can be approved on or after its effective date.");
        ApartmentUnit target=units.findByIdForUpdate(transfer.getApartmentUnit().getId())
            .orElseThrow(()->new ResourceNotFoundException("Target apartment unit not found."));
        UnitResidency current=transfer.getCurrentResidency()==null?null:residencies.findByIdForUpdate(transfer.getCurrentResidency().getId()).orElse(null);
        if(current==null||current.getStatus()!=ResidencyStatus.ACTIVE||!current.getResidentUser().getId().equals(transfer.getProposedUser().getId()))
            throw new ResourceConflictException("The resident's current residency has changed. This transfer cannot be approved.");
        if(transfer.getEffectiveDate().isBefore(current.getStartDate()))throw new ResourceConflictException("The transfer date cannot be before the current residency started.");
        if(residencies.existsByApartmentUnitIdAndStatus(target.getId(),ResidencyStatus.ACTIVE))
            throw new ResourceConflictException("The target unit already has an active resident.");

        LocalDate previousEnd=transfer.getEffectiveDate().minusDays(1);
        current.setStatus(ResidencyStatus.ENDED);
        current.setEndDate(previousEnd.isBefore(current.getStartDate())?transfer.getEffectiveDate():previousEnd);
        UnitResidency next=new UnitResidency(target,transfer.getProposedUser(),transfer.getResidencyType(),
            transfer.getEffectiveDate(),ResidencyStatus.ACTIVE);
        next.setVerifiedBy(admin);next.setVerifiedAt(Instant.now());residencies.save(next);
        transfer.setStatus(TransferStatus.APPROVED);
        return ResidencyTransferResponse.from(transfer);
    }

    private User activeUser(Long id){return users.findById(id).filter(User::isActive).orElseThrow(()->new ResourceNotFoundException("Active user account not found."));}
    private Pageable pageable(int page,int size){return PageRequest.of(page,size,Sort.by(Sort.Direction.ASC,"createdAt"));}
    private void validatePage(int page,int size){if(page<0||size<1||size>100)throw new IllegalArgumentException("Page must be non-negative and size must be between 1 and 100.");}
}
