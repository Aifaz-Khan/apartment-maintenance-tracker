package com.example.maintenance.residency.api;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record ResidencyTransferCreateRequest(@NotNull Long targetApartmentUnitId,
    @NotNull @FutureOrPresent LocalDate effectiveDate, @NotBlank @Size(max=2000) String reason) {}
