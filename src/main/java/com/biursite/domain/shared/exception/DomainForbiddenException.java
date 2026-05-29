package com.biursite.domain.shared.exception;

public class DomainForbiddenException extends RuntimeException {
    public DomainForbiddenException(String message) {
        super(message);
    }
}
