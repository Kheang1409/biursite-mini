package com.biursite.application.user.usecase;

import com.biursite.domain.user.repository.UserRepositoryPort;
import com.biursite.application.user.dto.UpdateUserRequest;
import com.biursite.application.user.dto.UserDto;
import com.biursite.application.user.mapper.UserDtoMapper;
import com.biursite.domain.user.service.PasswordHasher;
import com.biursite.application.shared.exception.ResourceNotFoundException;
import com.biursite.domain.shared.event.DomainEventPublisher;
import com.biursite.domain.user.event.UserUpdatedEvent;
import com.biursite.domain.shared.exception.ConcurrencyConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import jakarta.persistence.OptimisticLockException;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
public class UpdateUserService implements UpdateUserUseCase {
    private final UserRepositoryPort userRepository;
    private final PasswordHasher passwordHasher;
    private final UserDtoMapper userDtoMapper;
    private final DomainEventPublisher eventPublisher;

    @Override
    public UserDto execute(Long id, UpdateUserRequest request) {
        var existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (request.getVersion() != null && existing.getVersion() != null && !request.getVersion().equals(existing.getVersion())) {
            throw new ConcurrencyConflictException("User was updated by another request");
        }

        existing.updateProfile(request.getUsername(), request.getEmail());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            existing.updatePassword(passwordHasher.hash(request.getPassword()));
        }

        var saved = saveWithConflictHandling(existing, "User was updated by another request");
        eventPublisher.publish(new UserUpdatedEvent(saved.getId(), saved.getUsername(), saved.getEmail()));
        return userDtoMapper.toDto(saved);
    }

    private com.biursite.domain.user.entity.User saveWithConflictHandling(com.biursite.domain.user.entity.User user, String message) {
        try {
            return userRepository.save(user);
        } catch (ObjectOptimisticLockingFailureException | OptimisticLockException ex) {
            throw new ConcurrencyConflictException(message);
        }
    }
}
