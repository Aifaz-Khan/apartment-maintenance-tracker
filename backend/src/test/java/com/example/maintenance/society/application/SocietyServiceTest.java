package com.example.maintenance.society.application;

import com.example.maintenance.society.api.SocietyAdminAppointmentRequest;
import com.example.maintenance.society.api.SocietyCreateRequest;
import com.example.maintenance.society.domain.Society;
import com.example.maintenance.society.domain.SocietyAdmin;
import com.example.maintenance.society.infrastructure.SocietyAdminRepository;
import com.example.maintenance.society.infrastructure.SocietyRepository;
import com.example.maintenance.user.domain.UserRole;
import com.example.maintenance.user.infrastructure.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SocietyServiceTest {
    private final SocietyRepository societies=mock(SocietyRepository.class);
    private final SocietyAdminRepository admins=mock(SocietyAdminRepository.class);
    private final UserRepository users=mock(UserRepository.class);
    private final PasswordEncoder passwords=mock(PasswordEncoder.class);
    private final SocietyService service=new SocietyService(societies,admins,users,passwords);

    @Test void createsSocietyWithTrimmedCodeAndNormalizedEmail() {
        when(societies.existsByCodeIgnoreCase("ABC")).thenReturn(false);
        when(societies.save(any(Society.class))).thenAnswer(call->call.getArgument(0));
        var response=service.create(new SocietyCreateRequest(" Example "," ABC "," 1 Main ",null," City "," State ","12345","ADMIN@EXAMPLE.COM"));
        assertEquals("ABC",response.code());
        assertEquals("admin@example.com",response.contactEmail());
        assertEquals("ACTIVE",response.status());
    }

    @Test void preventsDuplicateSocietyCode() {
        when(societies.existsByCodeIgnoreCase("ABC")).thenReturn(true);
        assertThrows(ResourceConflictException.class,()->service.create(new SocietyCreateRequest("Example","ABC","1 Main",null,"City","State","12345","admin@example.com")));
        verify(societies,never()).save(any());
    }

    @Test void appointsOnlyControlledSocietyAdminRoleAndHashesPassword() {
        Society society=new Society("Example","ABC","1 Main",null,"City","State","12345","admin@example.com");
        when(societies.findById(7L)).thenReturn(java.util.Optional.of(society));
        when(users.existsByEmailIgnoreCase("manager@example.com")).thenReturn(false);
        when(passwords.encode("initial-password-123")).thenReturn("bcrypt-hash");
        when(users.save(any())).thenAnswer(call->{var user=(com.example.maintenance.user.domain.User)call.getArgument(0);user.setId(4L);return user;});
        when(admins.save(any(SocietyAdmin.class))).thenAnswer(call->call.getArgument(0));
        var result=service.appointAdmin(7L,new SocietyAdminAppointmentRequest("manager@example.com","Manager",null,"initial-password-123"));
        assertEquals(4L,result.userId());
        verify(admins).save(argThat(mapping->mapping.getUser().getRole()==UserRole.SOCIETY_ADMIN && mapping.getSociety()==society));
        verify(passwords).encode("initial-password-123");
    }
}
