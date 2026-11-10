package com.example.maintenance.residency.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ResidencyTransferReviewRequest(@NotNull Action action,@Size(max=1000) String rejectionReason) {
    public enum Action { APPROVE, REJECT }
}
