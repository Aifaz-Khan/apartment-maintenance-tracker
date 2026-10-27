package com.example.maintenance.complaint.api;

import com.example.maintenance.complaint.application.ComplaintService;
import com.example.maintenance.society.api.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/v1/complaints")
public class ComplaintController {
    private final ComplaintService service;
    public ComplaintController(ComplaintService service){this.service=service;}

    @PostMapping @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasRole('RESIDENT')")
    public ComplaintResponse create(@Valid @RequestBody ComplaintCreateRequest request,@AuthenticationPrincipal Jwt jwt){
        return service.create(Long.valueOf(jwt.getSubject()),request);
    }

    @GetMapping @PreAuthorize("hasAnyRole('RESIDENT','SOCIETY_ADMIN')")
    public PageResponse<ComplaintResponse> list(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size,@AuthenticationPrincipal Jwt jwt){
        return service.list(Long.valueOf(jwt.getSubject()),page,size);
    }

    @GetMapping("/{id}") @PreAuthorize("hasAnyRole('RESIDENT','SOCIETY_ADMIN')")
    public ComplaintResponse get(@PathVariable Long id,@AuthenticationPrincipal Jwt jwt){return service.get(Long.valueOf(jwt.getSubject()),id);}

    @GetMapping("/{id}/history") @PreAuthorize("hasAnyRole('RESIDENT','SOCIETY_ADMIN')")
    public List<ComplaintStatusHistoryResponse> history(@PathVariable Long id,@AuthenticationPrincipal Jwt jwt){return service.getHistory(Long.valueOf(jwt.getSubject()),id);}

    @PatchMapping("/{id}/status") @PreAuthorize("hasRole('SOCIETY_ADMIN')")
    public ComplaintResponse updateStatus(@PathVariable Long id,@Valid @RequestBody ComplaintStatusUpdateRequest request,@AuthenticationPrincipal Jwt jwt){
        return service.updateStatus(Long.valueOf(jwt.getSubject()),id,request);
    }
}
