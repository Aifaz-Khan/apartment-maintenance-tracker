package com.example.maintenance.category.infrastructure;

import com.example.maintenance.category.domain.ComplaintCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<ComplaintCategory, Long> {

    List<ComplaintCategory> findByActiveTrueAndSocietyIsNullOrderByNameAsc();

    @Query("SELECT c FROM ComplaintCategory c WHERE c.active = true AND (c.society IS NULL OR c.society.id = :societyId) ORDER BY c.name ASC")
    List<ComplaintCategory> findActiveBySocietyOrGlobal(@Param("societyId") Long societyId);
}
