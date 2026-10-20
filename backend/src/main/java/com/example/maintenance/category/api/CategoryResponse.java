package com.example.maintenance.category.api;

import com.example.maintenance.category.domain.ComplaintCategory;

public record CategoryResponse(
        Long id,
        Long societyId,
        String name,
        String description,
        boolean isGlobal,
        boolean active
) {
    public static CategoryResponse fromEntity(ComplaintCategory entity) {
        Long societyId = entity.getSociety() != null ? entity.getSociety().getId() : null;
        return new CategoryResponse(
                entity.getId(),
                societyId,
                entity.getName(),
                entity.getDescription(),
                societyId == null,
                entity.isActive()
        );
    }
}
