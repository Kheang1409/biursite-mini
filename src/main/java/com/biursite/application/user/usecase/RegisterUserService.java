package com.biursite.application.user.usecase;

import com.biursite.domain.user.entity.User;
import com.biursite.domain.user.entity.Role;
import com.biursite.application.shared.exception.BadRequestException;
import com.biursite.domain.user.repository.UserRepositoryPort;
import com.biursite.domain.user.service.PasswordHasher;
import com.biursite.application.user.dto.CreateUserCommand;
import lombok.RequiredArgsConstructor;
import java.time.Instant;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
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

        User user = User.register(
            cmd.getUsername(),
            cmd.getEmail(),
            passwordHasher.hash(cmd.getPassword()),
            Role.ROLE_USER,
            Instant.now()
        );

        return userRepository.save(user);
    }
}
