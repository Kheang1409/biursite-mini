package com.biursite.application.user.usecase;

import com.biursite.domain.shared.event.DomainEventPublisher;
import com.biursite.domain.user.entity.Role;
import com.biursite.domain.user.entity.User;
import com.biursite.domain.user.repository.UserRepositoryPort;
import com.biursite.application.user.dto.CreateUserCommand;
import com.biursite.application.user.dto.UserDto;
import com.biursite.domain.user.event.UserRegisteredEvent;
import com.biursite.domain.user.service.PasswordHasher;

import java.time.Instant;

public class CreateUserUseCase {
    private final UserRepositoryPort userRepository;
    private final PasswordHasher passwordHasher;
    private final DomainEventPublisher eventPublisher;

    public CreateUserUseCase(UserRepositoryPort userRepository,
                             PasswordHasher passwordHasher,
                             DomainEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.eventPublisher = eventPublisher;
    }

    public UserDto execute(CreateUserCommand command) {
        User user = User.builder()
                .username(command.getUsername())
                .email(command.getEmail())
                .password(passwordHasher.hash(command.getPassword()))
                .role(Role.ROLE_USER)
                .createdAt(Instant.now())
                .build();

        User saved = userRepository.save(user);

        eventPublisher.publish(new UserRegisteredEvent(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail()
        ));

        return UserDto.builder()
                .id(saved.getId())
                .username(saved.getUsername())
                .email(saved.getEmail())
                .role(saved.getRole().name())
                .banned(saved.getBanned())
                .createdAt(saved.getCreatedAt())
                .build();
    }
}
