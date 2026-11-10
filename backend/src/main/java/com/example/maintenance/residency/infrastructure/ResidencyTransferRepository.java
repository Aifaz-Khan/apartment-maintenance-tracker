package com.example.maintenance.residency.infrastructure;

import com.example.maintenance.residency.domain.ResidencyTransferRequest;
import com.example.maintenance.residency.domain.TransferStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ResidencyTransferRepository extends JpaRepository<ResidencyTransferRequest,Long> {
    Page<ResidencyTransferRequest> findAllByProposedUserIdOrderByCreatedAtDesc(Long proposedUserId,Pageable pageable);
    Page<ResidencyTransferRequest> findAllByApartmentUnitSocietyIdAndStatusOrderByCreatedAtAsc(Long societyId,TransferStatus status,Pageable pageable);
    boolean existsByProposedUserIdAndStatus(Long proposedUserId,TransferStatus status);
    boolean existsByApartmentUnitIdAndStatus(Long apartmentUnitId,TransferStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select transfer from ResidencyTransferRequest transfer where transfer.id=:id")
    Optional<ResidencyTransferRequest> findByIdForUpdate(@Param("id") Long id);
}
