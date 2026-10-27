package com.example.maintenance.apartment.api;

import com.example.maintenance.apartment.domain.ApartmentUnit;

public record ApartmentUnitResponse(Long id, Long societyId, String buildingName, String unitNumber,
    Integer floorNumber, String unitLabel) {
    public static ApartmentUnitResponse from(ApartmentUnit unit) {
        return new ApartmentUnitResponse(unit.getId(), unit.getSociety().getId(), unit.getBuildingName(),
            unit.getUnitNumber(), unit.getFloorNumber(), unit.getUnitLabel());
    }
}
