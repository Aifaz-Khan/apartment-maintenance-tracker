package com.example.maintenance.residency.infrastructure;
import com.example.maintenance.residency.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface UnitResidencyRepository extends JpaRepository<UnitResidency,Long>{Optional<UnitResidency> findByResidentUserIdAndStatus(Long userId, ResidencyStatus status);}
