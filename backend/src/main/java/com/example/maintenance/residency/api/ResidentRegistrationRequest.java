package com.example.maintenance.residency.api;

import com.example.maintenance.residency.domain.ResidencyType;
import jakarta.validation.constraints.*;

public record ResidentRegistrationRequest(
    @NotNull Long societyId,
    @NotNull Long apartmentUnitId,
    @NotBlank @Email @Size(max=150) String email,
    @NotBlank @Size(min=12,max=72) String password,
    @NotBlank @Size(max=150) String fullName,
    @Size(max=20) String phone,
    @NotNull ResidencyType residencyType
) {}
