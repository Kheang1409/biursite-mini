package com.biursite.application.query.dto;

import java.time.Instant;

public record PostDetailDto(
        Long id,
        Long version,
        String title,
        String content,
        String authorName,
        Long authorId,
        Instant createdAt,
        Instant updatedAt,
        Boolean banned,
        String banReason
) {
}
