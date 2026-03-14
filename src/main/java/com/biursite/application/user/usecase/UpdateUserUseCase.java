package com.biursite.application.user.usecase;

import com.biursite.domain.user.entity.User;
import com.biursite.domain.user.repository.UserRepositoryPort;
import com.biursite.application.user.dto.UpdateUserRequest;
import com.biursite.exception.ResourceNotFoundException;
import com.biursite.domain.user.service.PasswordHasher;
import org.springframework.stereotype.Component;

@Component
public class UpdateUserUseCase {
    private final UserRepositoryPort userRepository;
    private final PasswordHasher passwordHasher;

    public UpdateUserUseCase(UserRepositoryPort userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    public User execute(Long id, UpdateUserRequest request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        existing.setEmail(request.getEmail());
        existing.setUsername(request.getUsername());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            existing.setPassword(passwordHasher.hash(request.getPassword()));
        }

        return userRepository.save(existing);
    }
}
