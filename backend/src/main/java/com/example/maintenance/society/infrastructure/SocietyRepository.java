package com.example.maintenance.society.infrastructure;

import com.example.maintenance.society.domain.Society;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SocietyRepository extends JpaRepository<Society, Long>, JpaSpecificationExecutor<Society> {
    boolean existsByCodeIgnoreCase(String code);
}
