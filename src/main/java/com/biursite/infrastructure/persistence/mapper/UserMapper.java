package com.biursite.infrastructure.persistence.mapper;

import com.biursite.application.user.dto.UserDto;
import com.biursite.domain.user.entity.User;

public final class UserMapper {
    private UserMapper() {}

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole() == null ? null : user.getRole().name())
                .banned(Boolean.TRUE.equals(user.getBanned()))
                .createdAt(user.getCreatedAt())
                .build();
    }
}
