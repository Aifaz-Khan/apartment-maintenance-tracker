package com.example.maintenance.attachment.api;

import com.example.maintenance.attachment.application.AttachmentService;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController @Validated @RequestMapping("/api/v1/complaints/{complaintId}/attachments")
public class AttachmentController {
    private final AttachmentService service;
    public AttachmentController(AttachmentService service){this.service=service;}

    @PostMapping(consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('RESIDENT','SOCIETY_ADMIN')")
    public ResponseEntity<AttachmentResponse> upload(@PathVariable Long complaintId,
        @RequestPart("file") @NotNull MultipartFile file,@AuthenticationPrincipal Jwt jwt){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.upload(Long.valueOf(jwt.getSubject()),complaintId,file));
    }

    @GetMapping @PreAuthorize("hasAnyRole('RESIDENT','SOCIETY_ADMIN')")
    public List<AttachmentResponse> list(@PathVariable Long complaintId,@AuthenticationPrincipal Jwt jwt){
        return service.list(Long.valueOf(jwt.getSubject()),complaintId);
    }

    @GetMapping("/{attachmentId}") @PreAuthorize("hasAnyRole('RESIDENT','SOCIETY_ADMIN')")
    public ResponseEntity<ByteArrayResource> download(@PathVariable Long complaintId,@PathVariable Long attachmentId,
        @AuthenticationPrincipal Jwt jwt){
        AttachmentService.Download download=service.download(Long.valueOf(jwt.getSubject()),complaintId,attachmentId);
        var attachment=download.attachment();
        ContentDisposition disposition=ContentDisposition.attachment().filename(attachment.getOriginalFilename()).build();
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(attachment.getContentType()))
            .contentLength(download.bytes().length).header(HttpHeaders.CONTENT_DISPOSITION,disposition.toString())
            .header("X-Content-Type-Options","nosniff").body(new ByteArrayResource(download.bytes()));
    }
}
