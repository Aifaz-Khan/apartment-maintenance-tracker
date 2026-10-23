package com.example.maintenance.auth.application;
import com.example.maintenance.auth.api.*;
import com.example.maintenance.apartment.domain.ApartmentUnit;
import com.example.maintenance.residency.domain.*;
import com.example.maintenance.residency.infrastructure.UnitResidencyRepository;
import com.example.maintenance.society.domain.*;
import com.example.maintenance.society.infrastructure.SocietyAdminRepository;
import com.example.maintenance.user.domain.User;
import com.example.maintenance.user.infrastructure.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;

@Service
public class AuthService {
 private final UserRepository users; private final SocietyAdminRepository admins; private final UnitResidencyRepository residencies; private final PasswordEncoder passwords; private final JwtEncoder encoder; private final String issuer; private final long expiration;
 public AuthService(UserRepository users,SocietyAdminRepository admins,UnitResidencyRepository residencies,PasswordEncoder passwords,JwtEncoder encoder,@Value("${app.security.jwt.issuer}") String issuer,@Value("${app.security.jwt.expiration-seconds}") long expiration){this.users=users;this.admins=admins;this.residencies=residencies;this.passwords=passwords;this.encoder=encoder;this.issuer=issuer;this.expiration=expiration;}
 @Transactional(readOnly=true) public LoginResponse login(LoginRequest request){User user=users.findByEmailIgnoreCase(request.email().trim()).orElse(null); if(user==null || !user.isActive() || !passwords.matches(request.password(),user.getPasswordHash())) throw new InvalidCredentialsException(); Instant now=Instant.now(); JwtClaimsSet claims=JwtClaimsSet.builder().issuer(issuer).subject(user.getId().toString()).issuedAt(now).expiresAt(now.plusSeconds(expiration)).claim("email",user.getEmail()).claim("role",user.getRole().name()).claim("fullName",user.getFullName()).build(); String token=encoder.encode(JwtEncoderParameters.from(JwsHeader.with(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256).build(),claims)).getTokenValue(); return new LoginResponse(token,"Bearer",expiration,profile(user));}
 @Transactional(readOnly=true) public UserSummaryDto getCurrentUser(Long id){return users.findById(id).filter(User::isActive).map(this::profile).orElseThrow(InvalidCredentialsException::new);}
 private UserSummaryDto profile(User user){Long societyId=null,unitId=null;String societyName=null,unitNumber=null,buildingName=null; SocietyAdmin admin=admins.findByUserId(user.getId()).orElse(null); if(admin!=null){Society society=admin.getSociety();societyId=society.getId();societyName=society.getName();} UnitResidency residency=residencies.findByResidentUserIdAndStatus(user.getId(),ResidencyStatus.ACTIVE).orElse(null); if(residency!=null){ApartmentUnit unit=residency.getApartmentUnit();unitId=unit.getId();unitNumber=unit.getUnitNumber();buildingName=unit.getBuildingName();Society society=unit.getSociety();societyId=society.getId();societyName=society.getName();} return new UserSummaryDto(user.getId(),user.getEmail(),user.getFullName(),user.getPhone(),user.getRole().name(),societyId,societyName,unitId,unitNumber,buildingName);}
}
