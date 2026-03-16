package com.biursite.application.user.usecase;

import com.biursite.domain.user.repository.UserRepositoryPort;
import com.biursite.application.user.dto.UpdateUserRequest;
import com.biursite.application.user.dto.UserDto;
import com.biursite.application.user.mapper.UserDtoMapper;
import com.biursite.domain.user.service.PasswordHasher;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateUserService implements UpdateUserUseCase {
    private final UserRepositoryPort userRepository;
    private final PasswordHasher passwordHasher;
    private final UserDtoMapper userDtoMapper;

    @Override
    public UserDto execute(Long id, UpdateUserRequest request) {
        var existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existing.setEmail(request.getEmail());
        existing.setUsername(request.getUsername());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            existing.setPassword(passwordHasher.hash(request.getPassword()));
        }

        var saved = userRepository.save(existing);
        return userDtoMapper.toDto(saved);
    }
}
