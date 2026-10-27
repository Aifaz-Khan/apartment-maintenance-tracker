package com.example.maintenance.complaint.application;

import com.example.maintenance.apartment.domain.ApartmentUnit;
import com.example.maintenance.category.domain.ComplaintCategory;
import com.example.maintenance.category.infrastructure.CategoryRepository;
import com.example.maintenance.complaint.api.ComplaintCreateRequest;
import com.example.maintenance.complaint.domain.*;
import com.example.maintenance.complaint.infrastructure.ComplaintRepository;
import com.example.maintenance.residency.domain.*;
import com.example.maintenance.residency.infrastructure.UnitResidencyRepository;
import com.example.maintenance.society.domain.Society;
import com.example.maintenance.user.domain.*;
import com.example.maintenance.user.infrastructure.UserRepository;
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
    private final ComplaintService service=new ComplaintService(residencies,users,categories,complaints);

    @Test void createsComplaintUsingServerResolvedResidentAndUnitContext(){
        Society society=new Society("North","N","Address",null,"City","State","1","admin@example.com");society.setId(2L);
        ApartmentUnit unit=new ApartmentUnit(society,"A","101",1,null);unit.setId(5L);
        User resident=new User("resident@example.com","hash","Resident",null,UserRole.RESIDENT);resident.setId(7L);
        UnitResidency residency=new UnitResidency(unit,resident,ResidencyType.OWNER_OCCUPIED,LocalDate.now(),ResidencyStatus.ACTIVE);
        ComplaintCategory category=new ComplaintCategory(null,"Plumbing","Leaks");category.setId(3L);
        when(users.findById(7L)).thenReturn(Optional.of(resident));when(residencies.findByResidentUserIdAndStatus(7L,ResidencyStatus.ACTIVE)).thenReturn(Optional.of(residency));
        when(categories.findById(3L)).thenReturn(Optional.of(category));when(complaints.existsByApartmentUnitIdAndStatusIn(eq(5L),anyCollection())).thenReturn(false);
        when(complaints.saveAndFlush(any())).thenAnswer(call->{Complaint c=call.getArgument(0);c.setId(11L);return c;});
        var response=service.create(7L,new ComplaintCreateRequest(3L,"  Leaking pipe "," Water under the sink ","Kitchen",null,ComplaintPriority.HIGH));
        assertEquals(2L,response.societyId());assertEquals(5L,response.apartmentUnitId());assertEquals(7L,response.createdBy());
        assertEquals("Leaking pipe",response.title());assertEquals("SUBMITTED",response.status());
    }

    @Test void rejectsSecondActiveComplaintWithConflict(){
        Society society=new Society("North","N","Address",null,"City","State","1","admin@example.com");
        ApartmentUnit unit=new ApartmentUnit(society,"A","101",1,null);unit.setId(5L);
        User resident=new User("resident@example.com","hash","Resident",null,UserRole.RESIDENT);resident.setId(7L);
        when(users.findById(7L)).thenReturn(Optional.of(resident));when(residencies.findByResidentUserIdAndStatus(7L,ResidencyStatus.ACTIVE)).thenReturn(Optional.of(new UnitResidency(unit,resident,ResidencyType.TENANT,LocalDate.now(),ResidencyStatus.ACTIVE)));
        when(categories.findById(3L)).thenReturn(Optional.of(new ComplaintCategory(null,"Plumbing","Leaks")));
        when(complaints.existsByApartmentUnitIdAndStatusIn(eq(5L),anyCollection())).thenReturn(true);
        assertThrows(ActiveComplaintExistsException.class,()->service.create(7L,new ComplaintCreateRequest(3L,"Title","Description","Kitchen",null,null)));
        verify(complaints,never()).saveAndFlush(any());
    }

    @Test void rejectsCategoryOwnedByDifferentSociety(){
        Society society=new Society("North","N","Address",null,"City","State","1","admin@example.com");society.setId(2L);
        Society other=new Society("South","S","Address",null,"City","State","2","other@example.com");other.setId(8L);
        ApartmentUnit unit=new ApartmentUnit(society,"A","101",1,null);
        User resident=new User("resident@example.com","hash","Resident",null,UserRole.RESIDENT);resident.setId(7L);
        when(users.findById(7L)).thenReturn(Optional.of(resident));when(residencies.findByResidentUserIdAndStatus(7L,ResidencyStatus.ACTIVE)).thenReturn(Optional.of(new UnitResidency(unit,resident,ResidencyType.TENANT,LocalDate.now(),ResidencyStatus.ACTIVE)));
        when(categories.findById(3L)).thenReturn(Optional.of(new ComplaintCategory(other,"Private","Other society")));
        assertThrows(com.example.maintenance.society.application.ResourceNotFoundException.class,()->service.create(7L,new ComplaintCreateRequest(3L,"Title","Description","Kitchen",null,null)));
    }
}
