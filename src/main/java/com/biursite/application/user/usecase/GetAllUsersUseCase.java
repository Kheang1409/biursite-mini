package com.biursite.application.user.usecase;

import com.biursite.application.user.dto.UserDto;
import com.biursite.application.user.mapper.UserDtoMapper;
import com.biursite.domain.user.repository.UserRepositoryPort;

import java.util.List;

public class GetAllUsersUseCase {
    private final UserRepositoryPort userRepository;
    private final UserDtoMapper userDtoMapper;

    public GetAllUsersUseCase(UserRepositoryPort userRepository, UserDtoMapper userDtoMapper) {
        this.userRepository = userRepository;
        this.userDtoMapper = userDtoMapper;
    }

    public List<UserDto> execute() {
        return userRepository.findAll().stream().map(userDtoMapper::toDto).toList();
    }
}
