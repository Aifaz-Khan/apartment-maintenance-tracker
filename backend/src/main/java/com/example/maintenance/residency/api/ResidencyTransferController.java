package com.example.maintenance.residency.api;

import com.example.maintenance.residency.application.ResidencyTransferService;
import com.example.maintenance.society.api.PageResponse;
import com.example.maintenance.apartment.api.ApartmentUnitResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/residency-transfers") @PreAuthorize("hasRole('RESIDENT')")
public class ResidencyTransferController {
    private final ResidencyTransferService service;
    public ResidencyTransferController(ResidencyTransferService service){this.service=service;}

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public ResidencyTransferResponse submit(@Valid @RequestBody ResidencyTransferCreateRequest request,
        @AuthenticationPrincipal Jwt jwt){return service.submit(userId(jwt),request);}

    @GetMapping
    public PageResponse<ResidencyTransferResponse> mine(@RequestParam(defaultValue="0") int page,
        @RequestParam(defaultValue="20") int size,@AuthenticationPrincipal Jwt jwt){return service.mine(userId(jwt),page,size);}

    @GetMapping("/available-units")
    public PageResponse<ApartmentUnitResponse> availableUnits(@RequestParam(defaultValue="0") int page,
        @RequestParam(defaultValue="100") int size,@AuthenticationPrincipal Jwt jwt){return service.availableUnits(userId(jwt),page,size);}

    private Long userId(Jwt jwt){return Long.valueOf(jwt.getSubject());}
}
