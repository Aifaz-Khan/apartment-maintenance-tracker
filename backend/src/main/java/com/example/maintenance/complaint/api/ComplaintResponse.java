package com.example.maintenance.complaint.api;

import com.example.maintenance.complaint.domain.Complaint;
import java.time.Instant;

public record ComplaintResponse(Long id,Long societyId,Long apartmentUnitId,Long categoryId,String categoryName,
    Long createdBy,String title,String description,String locationArea,String locationDetails,
    String priority,String status,Instant createdAt) {
    public static ComplaintResponse from(Complaint c){
        return new ComplaintResponse(c.getId(),c.getSociety().getId(),c.getApartmentUnit().getId(),c.getCategory().getId(),
            c.getCategory().getName(),c.getCreatedBy().getId(),c.getTitle(),c.getDescription(),c.getLocationArea(),
            c.getLocationDetails(),c.getPriority().name(),c.getStatus().name(),c.getCreatedAt());
    }
}
