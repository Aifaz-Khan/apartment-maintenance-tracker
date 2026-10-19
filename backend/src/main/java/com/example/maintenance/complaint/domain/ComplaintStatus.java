package com.example.maintenance.complaint.domain;

public enum ComplaintStatus {
    SUBMITTED,
    ACKNOWLEDGED,
    IN_PROGRESS,
    ON_HOLD,
    RESOLVED,
    CLOSED,
    REOPENED,
    CANCELLED;

    public boolean isActive() {
        return this != CLOSED && this != CANCELLED;
    }
}
