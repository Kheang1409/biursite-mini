package com.biursite.application.user.mapper;

import com.biursite.application.user.dto.UserDto;
import com.biursite.domain.user.entity.User;

import java.util.List;

public interface UserDtoMapper {
    UserDto toDto(User user);
    default List<UserDto> toDtoList(List<User> users) {
        return users.stream().map(this::toDto).toList();
    }
}
