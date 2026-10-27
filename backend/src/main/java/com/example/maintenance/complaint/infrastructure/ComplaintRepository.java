package com.example.maintenance.complaint.infrastructure;

import com.example.maintenance.complaint.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;

public interface ComplaintRepository extends JpaRepository<Complaint,Long> {
    boolean existsByApartmentUnitIdAndStatusIn(Long apartmentUnitId, Collection<ComplaintStatus> statuses);
}
