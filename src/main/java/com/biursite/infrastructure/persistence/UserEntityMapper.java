package com.biursite.infrastructure.persistence;

import com.biursite.domain.user.entity.User;
import java.time.Instant;

public final class UserEntityMapper {
    private UserEntityMapper() {}

    public static User toDomain(UserEntity e) {
        if (e == null) return null;
        return User.builder()
                .id(e.getId())
                .username(e.getUsername())
                .email(e.getEmail())
                .password(e.getPassword())
                .role(e.getRole())
                .banned(e.getBanned())
                .createdAt(e.getCreatedAt())
                .build();
    }

    public static UserEntity toEntity(User u) {
        if (u == null) return null;
        Instant created = u.getCreatedAt() == null ? Instant.now() : u.getCreatedAt();
        return UserEntity.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .password(u.getPassword())
                .role(u.getRole())
                .banned(u.getBanned() == null ? Boolean.FALSE : u.getBanned())
                .createdAt(created)
                .build();
    }
}
