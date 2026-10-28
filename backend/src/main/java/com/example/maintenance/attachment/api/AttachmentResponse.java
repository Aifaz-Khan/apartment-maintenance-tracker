package com.example.maintenance.attachment.api;

import com.example.maintenance.attachment.domain.ComplaintAttachment;
import java.time.Instant;

public record AttachmentResponse(Long id, Long complaintId, String originalFilename, String contentType,
                                 Long sizeBytes, String mediaKind, Instant createdAt, String downloadUrl) {
    public static AttachmentResponse from(ComplaintAttachment attachment) {
        return new AttachmentResponse(attachment.getId(), attachment.getComplaint().getId(),
            attachment.getOriginalFilename(), attachment.getContentType(), attachment.getSizeBytes(),
            attachment.getMediaKind().name(), attachment.getCreatedAt(),
            "/api/v1/complaints/" + attachment.getComplaint().getId() + "/attachments/" + attachment.getId());
    }
}
