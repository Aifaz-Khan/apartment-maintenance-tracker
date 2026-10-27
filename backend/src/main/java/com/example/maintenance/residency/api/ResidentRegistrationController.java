package com.example.maintenance.residency.api;

import com.example.maintenance.residency.application.ResidentRegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/auth/resident-registration-requests")
public class ResidentRegistrationController {
    private final ResidentRegistrationService service;
    public ResidentRegistrationController(ResidentRegistrationService service){this.service=service;}
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public ResidencyRequestResponse submit(@Valid @RequestBody ResidentRegistrationRequest request){return service.submit(request);}
}
