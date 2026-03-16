package com.biursite.application.user.usecase;

import com.biursite.application.shared.exception.ResourceNotFoundException;
import com.biursite.domain.user.repository.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RequiredArgsConstructor
public class DeactivateAccountService implements DeactivateAccountUseCase {
    private final UserRepositoryPort userRepository;

    @Override
    public void execute(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        user.setDeactivated(true);
        userRepository.save(user);

        // Clear Spring Security context to remove authentication in the current thread
        SecurityContextHolder.clearContext();

        // Try to invalidate the current HTTP session if present (covers non-controller callers)
        var attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            if (request != null) {
                HttpSession session = request.getSession(false);
                if (session != null) {
                    try {
                        session.invalidate();
                    } catch (IllegalStateException ignored) {
                    }
                }
            }
        }
    }
}
