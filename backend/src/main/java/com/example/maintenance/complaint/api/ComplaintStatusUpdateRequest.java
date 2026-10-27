package com.example.maintenance.complaint.api;

import com.example.maintenance.complaint.domain.ComplaintStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ComplaintStatusUpdateRequest(@NotNull ComplaintStatus newStatus,
    @Size(max=2000) String remarks, @Size(max=5000) String resolutionNotes) {}
