package com.example.maintenance.society.api;

import com.example.maintenance.society.domain.SocietyStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record SocietyUpdateRequest(
    @Size(max=150) String name,
    @Size(max=50) String code,
    @Size(max=255) String addressLine1,
    @Size(max=255) String addressLine2,
    @Size(max=100) String city,
    @Size(max=100) String state,
    @Size(max=20) String postalCode,
    @Email @Size(max=150) String contactEmail,
    SocietyStatus status
) {}
