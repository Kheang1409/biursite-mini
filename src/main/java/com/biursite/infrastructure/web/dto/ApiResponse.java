package com.biursite.infrastructure.web.dto;

import java.time.Instant;
import java.util.Map;

public record ApiResponse<T>(
        boolean success,
        int status,
        String error,
        String message,
        String path,
        Instant timestamp,
        T data,
        Map<String, Object> meta
) {
    public static <T> ApiResponse<T> success(int status, String message, String path, T data) {
        return success(status, message, path, data, null);
    }

    public static <T> ApiResponse<T> success(int status, String message, String path, T data, Map<String, Object> meta) {
        return new ApiResponse<>(true, status, null, message, path, Instant.now(), data, meta);
    }

    public static <T> ApiResponse<T> failure(int status, String error, String message, String path) {
        return failure(status, error, message, path, null);
    }

    public static <T> ApiResponse<T> failure(int status, String error, String message, String path, Map<String, Object> meta) {
        return new ApiResponse<>(false, status, error, message, path, Instant.now(), null, meta);
    }
}
