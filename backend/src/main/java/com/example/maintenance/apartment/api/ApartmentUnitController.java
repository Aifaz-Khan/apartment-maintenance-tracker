package com.example.maintenance.apartment.api;

import com.example.maintenance.apartment.application.ApartmentUnitService;
import com.example.maintenance.society.api.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/societies/{societyId}/units") @PreAuthorize("hasRole('SOCIETY_ADMIN')")
public class ApartmentUnitController {
    private final ApartmentUnitService service;
    public ApartmentUnitController(ApartmentUnitService service){this.service=service;}
    @GetMapping public PageResponse<ApartmentUnitResponse> list(@PathVariable Long societyId,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size,@AuthenticationPrincipal Jwt jwt){return service.list(Long.valueOf(jwt.getSubject()),societyId,page,size);}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public ApartmentUnitResponse create(@PathVariable Long societyId,@Valid @RequestBody ApartmentUnitRequest request,@AuthenticationPrincipal Jwt jwt){return service.create(Long.valueOf(jwt.getSubject()),societyId,request);}
}
