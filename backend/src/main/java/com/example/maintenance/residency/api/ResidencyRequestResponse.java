package com.example.maintenance.residency.api;

import com.example.maintenance.residency.domain.UnitResidency;
import java.time.LocalDate;

public record ResidencyRequestResponse(Long id,Long userId,String fullName,String email,String phone,
    Long societyId,Long apartmentUnitId,String societyName,String buildingName,String unitNumber,
    String residencyType,String status,String rejectionReason,LocalDate requestedStartDate) {
    public static ResidencyRequestResponse from(UnitResidency r){
        var user=r.getResidentUser();var unit=r.getApartmentUnit();var society=unit.getSociety();
        return new ResidencyRequestResponse(r.getId(),user.getId(),user.getFullName(),user.getEmail(),user.getPhone(),
            society.getId(),unit.getId(),society.getName(),unit.getBuildingName(),unit.getUnitNumber(),
            r.getResidencyType().name(),r.getStatus().name(),r.getRejectionReason(),r.getStartDate());
    }
}
