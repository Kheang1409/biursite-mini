package com.biursite.infrastructure.service;

import com.biursite.domain.user.repository.UserRepositoryPort;
import com.biursite.infrastructure.web.dto.AuthRequest;
import com.biursite.infrastructure.web.dto.AuthResponse;
import com.biursite.infrastructure.web.dto.RegisterRequest;
import com.biursite.domain.user.entity.User;
import com.biursite.application.shared.exception.BadRequestException;
import com.biursite.application.shared.exception.UnauthorizedException;
import com.biursite.infrastructure.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
// AuthService interface removed; implementation remains in infrastructure
import com.biursite.application.user.usecase.CreateUserUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements com.biursite.application.user.usecase.AuthenticateUserUseCase {
    private final UserRepositoryPort userRepository;
    private final CreateUserUseCase createUserUseCase;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepositoryPort userRepository,
                           CreateUserUseCase createUserUseCase,
                           JwtUtil jwtUtil,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.createUserUseCase = createUserUseCase;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest request) {
        String token = execute(request.getUsername(), request.getPassword());
        return new AuthResponse(token);
    }

    @Override
    @Transactional(readOnly = true)
    public String execute(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        return jwtUtil.generateToken(user.getUsername(), user.getRole().name());
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email taken");
        }

        var cmd = new com.biursite.application.user.dto.CreateUserCommand(
            request.getUsername(),
            request.getEmail(),
            request.getPassword()
        );
        var created = createUserUseCase.execute(cmd);

        String token = jwtUtil.generateToken(created.getUsername(), created.getRole());
        return new AuthResponse(token);
    }
}
