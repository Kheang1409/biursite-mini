package com.biursite.security;

import com.biursite.domain.user.repository.UserRepositoryPort;
import com.biursite.domain.user.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityService {
    private final UserRepositoryPort userRepository;

    public SecurityService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    public Long getCurrentUserId() {
        return getCurrentUser()
                .map(User::getId)
                .orElseThrow(() -> new IllegalStateException("No authenticated user"));
    }

    public Optional<User> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return Optional.empty();
        }
        return userRepository.findByUsername(auth.getName());
    }
}
