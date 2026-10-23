package com.example.maintenance.society.api;

import com.example.maintenance.society.application.SocietyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/super-admin/societies")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminSocietyController {
    private final SocietyService service;
    public SuperAdminSocietyController(SocietyService service) { this.service=service; }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public SocietyResponse create(@Valid @RequestBody SocietyCreateRequest request) { return service.create(request); }

    @GetMapping public PageResponse<SocietyResponse> list(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="20") int size) {
        if (page<0 || size<1 || size>100) throw new IllegalArgumentException("Page must be non-negative and size must be between 1 and 100.");
        return service.list(page,size);
    }

    @GetMapping("/{id}") public SocietyResponse get(@PathVariable Long id) { return service.get(id); }
    @PatchMapping("/{id}") public SocietyResponse update(@PathVariable Long id, @Valid @RequestBody SocietyUpdateRequest request) { return service.update(id,request); }

    @PostMapping("/{id}/admins") @ResponseStatus(HttpStatus.CREATED)
    public SocietyAdminResponse appointAdmin(@PathVariable Long id, @Valid @RequestBody SocietyAdminAppointmentRequest request) { return service.appointAdmin(id,request); }
}
