package com.example.maintenance.apartment.infrastructure;

import com.example.maintenance.apartment.domain.ApartmentUnit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApartmentUnitRepository extends JpaRepository<ApartmentUnit, Long> {
    Page<ApartmentUnit> findAllBySocietyId(Long societyId, Pageable pageable);
    boolean existsBySocietyIdAndBuildingNameIgnoreCaseAndUnitNumberIgnoreCase(Long societyId, String buildingName, String unitNumber);
}
