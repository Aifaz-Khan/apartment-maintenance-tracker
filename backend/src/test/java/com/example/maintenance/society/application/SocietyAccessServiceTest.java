package com.example.maintenance.society.application;

import com.example.maintenance.society.domain.*;
import com.example.maintenance.society.infrastructure.SocietyAdminRepository;
import com.example.maintenance.user.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SocietyAccessServiceTest {
 @Test void onlyReturnsMappedSocietyAndRejectsCrossTenantId() {
  SocietyAdminRepository repository=mock(SocietyAdminRepository.class);
  Society society=new Society("North","N","Address",null,"City","State","1","a@example.com");
  SocietyAdmin mapping=new SocietyAdmin(new User("a@example.com","hash","Admin",null,com.example.maintenance.user.domain.UserRole.SOCIETY_ADMIN),society);
  mapping.setSociety(society); society.setId(10L);
  when(repository.findByUserId(9L)).thenReturn(Optional.of(mapping));
  SocietyAccessService service=new SocietyAccessService(repository);
  assertEquals(10L,service.requireSocietyScope(9L,10L));
  assertThrows(AccessDeniedException.class,()->service.requireSocietyScope(9L,11L));
 }
}
