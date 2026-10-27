package com.example.maintenance.residency.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ResidentRequestReview(@NotNull Action action, @Size(max=500) String rejectionReason) {
    public enum Action { APPROVE, REJECT }
}
