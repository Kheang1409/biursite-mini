package com.biursite.infrastructure.persistence.mapper;

import com.biursite.application.user.dto.UserDto;
import com.biursite.application.user.mapper.UserDtoMapper;
import com.biursite.domain.user.entity.User;

public class UserDtoMapperImpl implements UserDtoMapper {
    @Override
    public UserDto toDto(User user) {
        return UserMapper.toDto(user);
    }
}
