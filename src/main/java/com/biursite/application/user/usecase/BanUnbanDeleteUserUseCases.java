package com.biursite.application.user.usecase;

import com.biursite.domain.user.entity.User;
import com.biursite.domain.user.repository.UserRepositoryPort;
import com.biursite.application.shared.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
public class BanUnbanDeleteUserUseCases {
    private final UserRepositoryPort userRepository;

    public BanUnbanDeleteUserUseCases(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User ban(Long id) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        existing.setBanned(true);
        return userRepository.save(existing);
    }

    @Transactional
    public User unban(Long id) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        existing.setBanned(false);
        return userRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userRepository.delete(existing);
    }
}
