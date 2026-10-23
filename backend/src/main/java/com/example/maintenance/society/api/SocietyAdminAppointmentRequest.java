package com.example.maintenance.society.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SocietyAdminAppointmentRequest(
    @NotBlank @Email @Size(max=150) String email,
    @NotBlank @Size(max=150) String fullName,
    @Size(max=20) String phone,
    @NotBlank @Size(min=12,max=72) String initialPassword
) {}
