package com.example.maintenance.complaint.application;

import com.example.maintenance.apartment.domain.ApartmentUnit;
import com.example.maintenance.category.domain.ComplaintCategory;
import com.example.maintenance.category.infrastructure.CategoryRepository;
import com.example.maintenance.complaint.api.*;
import com.example.maintenance.complaint.domain.*;
import com.example.maintenance.complaint.infrastructure.*;
import com.example.maintenance.residency.domain.*;
import com.example.maintenance.residency.infrastructure.UnitResidencyRepository;
import com.example.maintenance.society.application.SocietyAccessService;
import com.example.maintenance.society.domain.Society;
import com.example.maintenance.user.domain.*;
import com.example.maintenance.user.infrastructure.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ComplaintServiceTest {
    private final UnitResidencyRepository residencies=mock(UnitResidencyRepository.class);
    private final UserRepository users=mock(UserRepository.class);
    private final CategoryRepository categories=mock(CategoryRepository.class);
    private final ComplaintRepository complaints=mock(ComplaintRepository.class);
    private final ComplaintStatusHistoryRepository history=mock(ComplaintStatusHistoryRepository.class);
    private final SocietyAccessService access=mock(SocietyAccessService.class);
    private final ApplicationEventPublisher events=mock(ApplicationEventPublisher.class);
    private final ComplaintService service=new ComplaintService(residencies,users,categories,complaints,history,access,events);

    @Test void createsComplaintUsingServerResolvedResidentAndUnitContext(){
        Society society=society();ApartmentUnit unit=new ApartmentUnit(society,"A","101",1,null);unit.setId(5L);
        User resident=new User("resident@example.com","hash","Resident",null,UserRole.RESIDENT);resident.setId(7L);
        UnitResidency residency=new UnitResidency(unit,resident,ResidencyType.OWNER_OCCUPIED,LocalDate.now(),ResidencyStatus.ACTIVE);
        ComplaintCategory category=new ComplaintCategory(null,"Plumbing","Leaks");category.setId(3L);
        when(users.findById(7L)).thenReturn(Optional.of(resident));when(residencies.findByResidentUserIdAndStatus(7L,ResidencyStatus.ACTIVE)).thenReturn(Optional.of(residency));
        when(categories.findById(3L)).thenReturn(Optional.of(category));when(complaints.existsByApartmentUnitIdAndStatusIn(eq(5L),anyCollection())).thenReturn(false);
        when(complaints.saveAndFlush(any())).thenAnswer(call->{Complaint c=call.getArgument(0);c.setId(11L);return c;});
        var response=service.create(7L,new ComplaintCreateRequest(3L,"  Leaking pipe "," Water under the sink ","Kitchen",null,ComplaintPriority.HIGH));
        assertEquals(2L,response.societyId());assertEquals(5L,response.apartmentUnitId());assertEquals(7L,response.createdBy());
        assertEquals("Leaking pipe",response.title());assertEquals("SUBMITTED",response.status());
        verify(history).save(argThat(h->h.getPreviousStatus()==null&&h.getNewStatus()==ComplaintStatus.SUBMITTED));
    }

    @Test void rejectsSecondActiveComplaintWithConflict(){
        Society society=society();ApartmentUnit unit=new ApartmentUnit(society,"A","101",1,null);unit.setId(5L);
        User resident=new User("resident@example.com","hash","Resident",null,UserRole.RESIDENT);resident.setId(7L);
        when(users.findById(7L)).thenReturn(Optional.of(resident));when(residencies.findByResidentUserIdAndStatus(7L,ResidencyStatus.ACTIVE)).thenReturn(Optional.of(new UnitResidency(unit,resident,ResidencyType.TENANT,LocalDate.now(),ResidencyStatus.ACTIVE)));
        when(categories.findById(3L)).thenReturn(Optional.of(new ComplaintCategory(null,"Plumbing","Leaks")));
        when(complaints.existsByApartmentUnitIdAndStatusIn(eq(5L),anyCollection())).thenReturn(true);
        assertThrows(ActiveComplaintExistsException.class,()->service.create(7L,new ComplaintCreateRequest(3L,"Title","Description","Kitchen",null,null)));
        verify(complaints,never()).saveAndFlush(any());
    }

    @Test void rejectsCategoryOwnedByDifferentSociety(){
        Society society=society();Society other=new Society("South","S","Address",null,"City","State","2","other@example.com");other.setId(8L);
        ApartmentUnit unit=new ApartmentUnit(society,"A","101",1,null);User resident=new User("resident@example.com","hash","Resident",null,UserRole.RESIDENT);resident.setId(7L);
        when(users.findById(7L)).thenReturn(Optional.of(resident));when(residencies.findByResidentUserIdAndStatus(7L,ResidencyStatus.ACTIVE)).thenReturn(Optional.of(new UnitResidency(unit,resident,ResidencyType.TENANT,LocalDate.now(),ResidencyStatus.ACTIVE)));
        when(categories.findById(3L)).thenReturn(Optional.of(new ComplaintCategory(other,"Private","Other society")));
        assertThrows(com.example.maintenance.society.application.ResourceNotFoundException.class,()->service.create(7L,new ComplaintCreateRequest(3L,"Title","Description","Kitchen",null,null)));
    }

    @Test void adminCanResolveWithNotesAndAuditRemark(){
        User admin=new User("admin@example.com","hash","Admin",null,UserRole.SOCIETY_ADMIN);admin.setId(9L);Society society=society();
        Complaint complaint=complaint(society,ComplaintStatus.IN_PROGRESS);when(users.findById(9L)).thenReturn(Optional.of(admin));when(complaints.findById(20L)).thenReturn(Optional.of(complaint));when(access.requireSocietyScope(9L,2L)).thenReturn(2L);
        var result=service.updateStatus(9L,20L,new ComplaintStatusUpdateRequest(ComplaintStatus.RESOLVED,"Technician dispatched","Leak fixed"));
        assertEquals("RESOLVED",result.status());assertEquals("Leak fixed",result.resolutionNotes());assertNotNull(complaint.getResolvedAt());
        verify(history).save(argThat(h->h.getPreviousStatus()==ComplaintStatus.IN_PROGRESS&&h.getNewStatus()==ComplaintStatus.RESOLVED&&"Technician dispatched".equals(h.getRemarks())));
    }

    @Test void rejectsInvalidTransitionAndResolutionWithoutNotes(){
        User admin=new User("admin@example.com","hash","Admin",null,UserRole.SOCIETY_ADMIN);admin.setId(9L);Complaint submitted=complaint(society(),ComplaintStatus.SUBMITTED);
        when(users.findById(9L)).thenReturn(Optional.of(admin));when(complaints.findById(20L)).thenReturn(Optional.of(submitted));when(access.requireSocietyScope(9L,2L)).thenReturn(2L);
        assertThrows(InvalidStatusTransitionException.class,()->service.updateStatus(9L,20L,new ComplaintStatusUpdateRequest(ComplaintStatus.CLOSED,null,null)));
        Complaint inProgress=complaint(society(),ComplaintStatus.IN_PROGRESS);when(complaints.findById(21L)).thenReturn(Optional.of(inProgress));
        assertThrows(IllegalArgumentException.class,()->service.updateStatus(9L,21L,new ComplaintStatusUpdateRequest(ComplaintStatus.RESOLVED,null," ")));
        verify(history,never()).save(any());
    }

    @Test void deniesAdminFromDifferentSociety(){
        User admin=new User("admin@example.com","hash","Admin",null,UserRole.SOCIETY_ADMIN);admin.setId(9L);
        when(users.findById(9L)).thenReturn(Optional.of(admin));when(complaints.findById(20L)).thenReturn(Optional.of(complaint(society(),ComplaintStatus.SUBMITTED)));
        doThrow(new org.springframework.security.access.AccessDeniedException("wrong tenant")).when(access).requireSocietyScope(9L,2L);
        assertThrows(org.springframework.security.access.AccessDeniedException.class,()->service.updateStatus(9L,20L,new ComplaintStatusUpdateRequest(ComplaintStatus.ACKNOWLEDGED,null,null)));
    }

    private Society society(){Society s=new Society("North","N","Address",null,"City","State","1","admin@example.com");s.setId(2L);return s;}
    private Complaint complaint(Society society,ComplaintStatus status){
        ApartmentUnit unit=new ApartmentUnit(society,"A","101",1,null);unit.setId(5L);User resident=new User("resident@example.com","hash","Resident",null,UserRole.RESIDENT);resident.setId(7L);
        ComplaintCategory category=new ComplaintCategory(null,"Plumbing","Leaks");category.setId(3L);
        Complaint c=new Complaint(society,unit,category,resident,"Leak","Description","Kitchen",null,ComplaintPriority.MEDIUM);c.setId(20L);c.setStatus(status);return c;
    }
}
