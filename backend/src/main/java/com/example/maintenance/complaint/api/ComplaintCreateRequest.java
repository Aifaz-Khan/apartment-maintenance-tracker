package com.example.maintenance.complaint.api;

import com.example.maintenance.complaint.domain.ComplaintPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ComplaintCreateRequest(
    @NotNull Long categoryId,
    @NotBlank @Size(max=200) String title,
    @NotBlank String description,
    @NotBlank @Size(max=100) String locationArea,
    String locationDetails,
    ComplaintPriority priority
) {}
