package com.example.maintenance.society.application;

import com.example.maintenance.society.infrastructure.SocietyAdminRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SocietyAccessService {
    private final SocietyAdminRepository admins;
    public SocietyAccessService(SocietyAdminRepository admins) { this.admins=admins; }

    @Transactional(readOnly=true)
    public Long requireAssignedSociety(Long userId) {
        return admins.findByUserId(userId).map(mapping->mapping.getSociety().getId())
            .orElseThrow(()->new AccessDeniedException("No society is assigned to this account."));
    }

    @Transactional(readOnly=true)
    public Long requireSocietyScope(Long userId, Long requestedSocietyId) {
        Long assigned=requireAssignedSociety(userId);
        if (!assigned.equals(requestedSocietyId)) throw new AccessDeniedException("This account cannot access another society.");
        return assigned;
    }
}
