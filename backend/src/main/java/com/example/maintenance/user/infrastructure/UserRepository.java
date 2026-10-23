package com.example.maintenance.user.infrastructure;

import com.example.maintenance.user.domain.User;
import com.example.maintenance.user.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByRole(UserRole role);
    boolean existsByEmailIgnoreCase(String email);
}
