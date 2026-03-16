package com.biursite.infrastructure.security;

import com.biursite.application.shared.security.CurrentUser;
import com.biursite.application.shared.security.CurrentUserPort;
import com.biursite.domain.user.repository.UserRepositoryPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityService implements CurrentUserPort {
    private final UserRepositoryPort userRepository;

    public SecurityService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Long getCurrentUserId() {
        return getCurrentUser()
                .map(CurrentUser::getId)
                .orElseThrow(() -> new com.biursite.application.shared.exception.UnauthorizedException("Not authenticated"));
    }

    @Override
    public Optional<CurrentUser> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return Optional.empty();
        }
        return userRepository.findByUsername(auth.getName()).map(u -> new CurrentUser(
            u.getId(),
            u.getUsername(),
            u.getEmail(),
            u.getRole() == null ? null : u.getRole().name(),
            u.getBanned(),
            u.getDeactivated(),
            u.getCreatedAt()
        ));
    }

    @Override
    public boolean currentUserHasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role));
    }
}
