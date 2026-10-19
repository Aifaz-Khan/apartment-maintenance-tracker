package com.example.maintenance.shared.exception;

public record FieldErrorItem(
        String field,
        String message
) {}
