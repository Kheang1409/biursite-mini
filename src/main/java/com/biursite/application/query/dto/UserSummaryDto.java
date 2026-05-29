package com.biursite.application.query.dto;

import com.biursite.domain.user.entity.Role;
import java.time.Instant;

public record UserSummaryDto(
        Long id,
        Long version,
        String username,
        String email,
        Role role,
        Boolean banned,
        Boolean deactivated,
        Instant createdAt
) {
}
