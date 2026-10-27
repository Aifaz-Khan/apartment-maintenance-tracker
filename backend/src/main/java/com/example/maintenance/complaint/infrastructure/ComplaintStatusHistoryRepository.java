package com.example.maintenance.complaint.infrastructure;

import com.example.maintenance.complaint.domain.ComplaintStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComplaintStatusHistoryRepository extends JpaRepository<ComplaintStatusHistory,Long> {
    List<ComplaintStatusHistory> findAllByComplaintIdOrderByCreatedAtAsc(Long complaintId);
}
