package com.example.maintenance.user.application;
import com.example.maintenance.user.domain.*;
import com.example.maintenance.user.infrastructure.UserRepository;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
@Configuration
public class SuperAdminBootstrapRunner {
 private static final Logger log=LoggerFactory.getLogger(SuperAdminBootstrapRunner.class);
 @Bean
 @ConditionalOnProperty(prefix="app.superadmin.bootstrap", name="enabled", havingValue="true", matchIfMissing=true)
 ApplicationRunner bootstrapSuperAdmin(UserRepository users,PasswordEncoder encoder,@Value("${app.superadmin.email}") String email,@Value("${app.superadmin.password}") String password){return args->createIfMissing(users,encoder,email,password);}
 @Transactional public void createIfMissing(UserRepository users,PasswordEncoder encoder,String email,String password){if(users.existsByRole(UserRole.SUPER_ADMIN))return;if(users.existsByEmailIgnoreCase(email))throw new IllegalStateException("Configured Super Admin email is already assigned to a non-admin account");users.save(new User(email,encoder.encode(password),"Platform Super Admin",null,UserRole.SUPER_ADMIN));log.info("Bootstrapped initial Super Admin account: {}",email);}
}
