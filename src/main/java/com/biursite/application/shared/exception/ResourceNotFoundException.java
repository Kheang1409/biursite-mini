package com.biursite.application.shared.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s not found: %s=%s", resource, field, String.valueOf(value)));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
