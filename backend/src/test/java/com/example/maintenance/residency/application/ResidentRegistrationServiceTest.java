package com.example.maintenance.residency.application;

import com.example.maintenance.apartment.domain.ApartmentUnit;
import com.example.maintenance.apartment.infrastructure.ApartmentUnitRepository;
import com.example.maintenance.residency.api.ResidentRequestReview;
import com.example.maintenance.residency.domain.*;
import com.example.maintenance.residency.infrastructure.UnitResidencyRepository;
import com.example.maintenance.society.application.SocietyAccessService;
import com.example.maintenance.society.domain.*;
import com.example.maintenance.society.infrastructure.SocietyAdminRepository;
import com.example.maintenance.user.domain.*;
import com.example.maintenance.user.infrastructure.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResidentRegistrationServiceTest {
    private final ApartmentUnitRepository units=mock(ApartmentUnitRepository.class);
    private final UnitResidencyRepository residencies=mock(UnitResidencyRepository.class);
    private final UserRepository users=mock(UserRepository.class);
    private final SocietyAdminRepository admins=mock(SocietyAdminRepository.class);
    private final SocietyAccessService access=mock(SocietyAccessService.class);
    private final PasswordEncoder passwords=mock(PasswordEncoder.class);
    private final ResidentRegistrationService service=new ResidentRegistrationService(units,residencies,users,admins,access,passwords);

    @Test void approvalActivatesResidentAndRecordsVerifier() {
        Society society=society();ApartmentUnit unit=new ApartmentUnit(society,"A","101",1,null);unit.setId(21L);
        User resident=new User("resident@example.com","hash","Resident",null,UserRole.RESIDENT);resident.setId(31L);resident.setActive(false);
        UnitResidency pending=new UnitResidency(unit,resident,ResidencyType.OWNER_OCCUPIED,LocalDate.now(),ResidencyStatus.PENDING);pending.setId(41L);
        User adminUser=new User("admin@example.com","hash","Admin",null,UserRole.SOCIETY_ADMIN);adminUser.setId(51L);
        SocietyAdmin mapping=new SocietyAdmin(adminUser,society);
        when(residencies.findById(41L)).thenReturn(Optional.of(pending));when(residencies.existsByApartmentUnitIdAndStatus(21L,ResidencyStatus.ACTIVE)).thenReturn(false);when(admins.findByUserId(51L)).thenReturn(Optional.of(mapping));when(access.requireSocietyScope(51L,12L)).thenReturn(12L);
        var response=service.review(51L,41L,new ResidentRequestReview(ResidentRequestReview.Action.APPROVE,null));
        assertEquals("ACTIVE",response.status());assertTrue(resident.isActive());assertSame(adminUser,pending.getVerifiedBy());assertNotNull(pending.getVerifiedAt());
    }

    @Test void rejectionRequiresAndStoresReason() {
        Society society=society();ApartmentUnit unit=new ApartmentUnit(society,"A","101",1,null);
        User resident=new User("resident@example.com","hash","Resident",null,UserRole.RESIDENT);resident.setActive(false);
        UnitResidency pending=new UnitResidency(unit,resident,ResidencyType.TENANT,LocalDate.now(),ResidencyStatus.PENDING);pending.setId(42L);
        when(residencies.findById(42L)).thenReturn(Optional.of(pending));when(access.requireSocietyScope(51L,12L)).thenReturn(12L);
        assertThrows(IllegalArgumentException.class,()->service.review(51L,42L,new ResidentRequestReview(ResidentRequestReview.Action.REJECT," ")));
        var response=service.review(51L,42L,new ResidentRequestReview(ResidentRequestReview.Action.REJECT," Lease not verified "));
        assertEquals("REJECTED",response.status());assertEquals("Lease not verified",response.rejectionReason());assertFalse(resident.isActive());
    }

    private Society society(){Society s=new Society("Society","S","Address",null,"City","State","1","admin@example.com");s.setId(12L);return s;}
}
