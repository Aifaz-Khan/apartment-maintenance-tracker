package com.example.maintenance.complaint.api;

import com.example.maintenance.complaint.application.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/complaints") @PreAuthorize("hasRole('RESIDENT')")
public class ComplaintController {
    private final ComplaintService service;
    public ComplaintController(ComplaintService service){this.service=service;}
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public ComplaintResponse create(@Valid @RequestBody ComplaintCreateRequest request,@AuthenticationPrincipal Jwt jwt){
        return service.create(Long.valueOf(jwt.getSubject()),request);
    }
}
