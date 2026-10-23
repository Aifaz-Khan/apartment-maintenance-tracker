package com.example.maintenance.auth.api;
import com.example.maintenance.auth.application.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/auth")
public class AuthController {
 private final AuthService service; public AuthController(AuthService service){this.service=service;}
 @PostMapping("/login") public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request){return ResponseEntity.ok(service.login(request));}
}
