package com.biursite.application.user.usecase;

import com.biursite.domain.user.entity.User;
import com.biursite.domain.user.entity.Role;
import com.biursite.application.shared.exception.BadRequestException;
import com.biursite.domain.user.repository.UserRepositoryPort;
import com.biursite.domain.user.service.PasswordHasher;
import com.biursite.application.user.dto.CreateUserCommand;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegisterUserService implements RegisterUserUseCase {
    private final UserRepositoryPort userRepository;
    private final PasswordHasher passwordHasher;

    @Override
    public User execute(CreateUserCommand cmd) {
        if (userRepository.existsByUsername(cmd.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }
        if (userRepository.existsByEmail(cmd.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        User user = User.builder()
            .username(cmd.getUsername())
            .email(cmd.getEmail())
            .password(passwordHasher.hash(cmd.getPassword()))
            .role(Role.ROLE_USER)
            .build();

        return userRepository.save(user);
    }
}
