package com.biursite.application.user.usecase;

import com.biursite.application.user.dto.UserDto;
import com.biursite.application.user.mapper.UserDtoMapper;
import com.biursite.domain.user.repository.UserRepositoryPort;
import com.biursite.application.shared.exception.ResourceNotFoundException;
public class GetUserByIdUseCase {
    private final UserRepositoryPort userRepository;
    private final UserDtoMapper userDtoMapper;

    public GetUserByIdUseCase(UserRepositoryPort userRepository, UserDtoMapper userDtoMapper) {
        this.userRepository = userRepository;
        this.userDtoMapper = userDtoMapper;
    }

    public UserDto execute(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return userDtoMapper.toDto(user);
    }
}
