package com.biursite.application.user.usecase;

import com.biursite.domain.user.entity.User;
import com.biursite.domain.user.repository.UserRepositoryPort;
import com.biursite.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class GetUserByIdUseCase {
    private final UserRepositoryPort userRepository;

    public GetUserByIdUseCase(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
}
