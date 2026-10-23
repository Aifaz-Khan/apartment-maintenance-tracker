package com.example.maintenance.user.api;
import com.example.maintenance.auth.api.UserSummaryDto;
import com.example.maintenance.auth.application.AuthService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/users")
public class UserController {
 private final AuthService service; public UserController(AuthService service){this.service=service;}
 @GetMapping("/me") public UserSummaryDto me(@AuthenticationPrincipal Jwt jwt){return service.getCurrentUser(Long.valueOf(jwt.getSubject()));}
}
