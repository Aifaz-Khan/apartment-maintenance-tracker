package com.example.maintenance.residency.infrastructure;

import com.example.maintenance.residency.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UnitResidencyRepository extends JpaRepository<UnitResidency,Long> {
    Optional<UnitResidency> findByResidentUserIdAndStatus(Long userId, ResidencyStatus status);
    Page<UnitResidency> findAllByApartmentUnitSocietyIdAndStatus(Long societyId, ResidencyStatus status, Pageable pageable);
    boolean existsByApartmentUnitIdAndStatus(Long apartmentUnitId, ResidencyStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select residency from UnitResidency residency where residency.id=:id")
    Optional<UnitResidency> findByIdForUpdate(@Param("id") Long id);
}
