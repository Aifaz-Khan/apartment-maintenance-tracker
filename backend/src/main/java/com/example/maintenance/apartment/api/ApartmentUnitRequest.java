package com.example.maintenance.apartment.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ApartmentUnitRequest(@NotBlank @Size(max=100) String buildingName,
    @NotBlank @Size(max=50) String unitNumber, Integer floorNumber, @Size(max=150) String unitLabel) {}
