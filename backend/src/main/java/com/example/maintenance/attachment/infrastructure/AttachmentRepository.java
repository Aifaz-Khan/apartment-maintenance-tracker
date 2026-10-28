package com.example.maintenance.attachment.infrastructure;

import com.example.maintenance.attachment.domain.ComplaintAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AttachmentRepository extends JpaRepository<ComplaintAttachment, Long> {
    List<ComplaintAttachment> findAllByComplaintIdOrderByCreatedAtAsc(Long complaintId);
    Optional<ComplaintAttachment> findByIdAndComplaintId(Long id, Long complaintId);
}
