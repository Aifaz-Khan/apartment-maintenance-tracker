package com.example.maintenance.society.infrastructure;
import com.example.maintenance.society.domain.SocietyAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface SocietyAdminRepository extends JpaRepository<SocietyAdmin,Long>{Optional<SocietyAdmin> findByUserId(Long userId);}
