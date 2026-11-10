package com.example.maintenance.residency.api;

import com.example.maintenance.residency.domain.ResidencyTransferRequest;
import java.time.Instant;
import java.time.LocalDate;

public record ResidencyTransferResponse(Long id,Long residentId,String residentName,String residentEmail,
    Long societyId,String societyName,Long currentApartmentUnitId,String currentBuildingName,String currentUnitNumber,
    Long targetApartmentUnitId,String targetBuildingName,String targetUnitNumber,String residencyType,
    LocalDate effectiveDate,String reason,String status,Long reviewedBy,String rejectionReason,
    Instant createdAt,Instant reviewedAt) {
    public static ResidencyTransferResponse from(ResidencyTransferRequest request){
        var resident=request.getProposedUser();var target=request.getApartmentUnit();var society=target.getSociety();
        var current=request.getCurrentResidency()==null?null:request.getCurrentResidency().getApartmentUnit();
        return new ResidencyTransferResponse(request.getId(),resident.getId(),resident.getFullName(),resident.getEmail(),
            society.getId(),society.getName(),current==null?null:current.getId(),current==null?null:current.getBuildingName(),
            current==null?null:current.getUnitNumber(),target.getId(),target.getBuildingName(),target.getUnitNumber(),
            request.getResidencyType().name(),request.getEffectiveDate(),request.getReason(),request.getStatus().name(),
            request.getReviewedBy()==null?null:request.getReviewedBy().getId(),request.getRejectionReason(),
            request.getCreatedAt(),request.getReviewedAt());
    }
}
