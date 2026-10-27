package com.example.maintenance.complaint.api;

import com.example.maintenance.complaint.domain.ComplaintStatusHistory;
import java.time.Instant;

public record ComplaintStatusHistoryResponse(Long id,Long changedBy,String changedByName,
    String previousStatus,String newStatus,String remarks,Instant createdAt) {
    public static ComplaintStatusHistoryResponse from(ComplaintStatusHistory history){
        return new ComplaintStatusHistoryResponse(history.getId(),history.getChangedBy().getId(),
            history.getChangedBy().getFullName(),history.getPreviousStatus()==null?null:history.getPreviousStatus().name(),
            history.getNewStatus().name(),history.getRemarks(),history.getCreatedAt());
    }
}
