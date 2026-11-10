package com.example.maintenance.apartment.infrastructure;

import com.example.maintenance.apartment.domain.ApartmentUnit;
import com.example.maintenance.residency.domain.ResidencyStatus;
import com.example.maintenance.residency.domain.ResidencyTransferRequest;
import com.example.maintenance.residency.domain.TransferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ApartmentUnitRepository extends JpaRepository<ApartmentUnit, Long> {
    Page<ApartmentUnit> findAllBySocietyId(Long societyId, Pageable pageable);
    boolean existsBySocietyIdAndBuildingNameIgnoreCaseAndUnitNumberIgnoreCase(Long societyId, String buildingName, String unitNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select unit from ApartmentUnit unit where unit.id=:id")
    Optional<ApartmentUnit> findByIdForUpdate(@Param("id") Long id);

    @Query("select unit from ApartmentUnit unit where unit.society.id=:societyId and unit.society.status='ACTIVE' and unit.id<>:currentUnitId " +
        "and not exists (select residency.id from UnitResidency residency where residency.apartmentUnit=unit and residency.status=:activeStatus) " +
        "and not exists (select transfer.id from ResidencyTransferRequest transfer where transfer.apartmentUnit=unit and transfer.status=:pendingStatus)")
    org.springframework.data.domain.Page<ApartmentUnit> findAvailableForTransfer(@Param("societyId") Long societyId,
        @Param("currentUnitId") Long currentUnitId,@Param("activeStatus") ResidencyStatus activeStatus,
        @Param("pendingStatus") TransferStatus pendingStatus,org.springframework.data.domain.Pageable pageable);
}
