package com.example.maintenance.residency.api;

import com.example.maintenance.residency.application.ResidencyTransferService;
import com.example.maintenance.society.api.PageResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/admin/residency-transfers") @PreAuthorize("hasRole('SOCIETY_ADMIN')")
public class ResidencyTransferAdminController {
    private final ResidencyTransferService service;
    public ResidencyTransferAdminController(ResidencyTransferService service){this.service=service;}

    @GetMapping
    public PageResponse<ResidencyTransferResponse> pending(@RequestParam(defaultValue="0") int page,
        @RequestParam(defaultValue="20") int size,@AuthenticationPrincipal Jwt jwt){
        return service.pending(Long.valueOf(jwt.getSubject()),page,size);
    }

    @PatchMapping("/{id}")
    public ResidencyTransferResponse review(@PathVariable Long id,@Valid @RequestBody ResidencyTransferReviewRequest request,
        @AuthenticationPrincipal Jwt jwt){return service.review(Long.valueOf(jwt.getSubject()),id,request);}
}
