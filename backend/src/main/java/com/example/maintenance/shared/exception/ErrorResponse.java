package com.example.maintenance.shared.exception;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path,
        List<FieldErrorItem> fieldErrors
) {
    public ErrorResponse(int status, String code, String message, String path) {
        this(Instant.now(), status, code, message, path, Collections.emptyList());
    }

    public ErrorResponse(int status, String code, String message, String path, List<FieldErrorItem> fieldErrors) {
        this(Instant.now(), status, code, message, path, fieldErrors != null ? fieldErrors : Collections.emptyList());
    }
}
