package com.biursite.application.query.dto;

import java.time.Instant;

public record PostSummaryDto(
        Long id,
        String title,
        String excerpt,
        String authorName,
        Instant createdAt
) {
}
