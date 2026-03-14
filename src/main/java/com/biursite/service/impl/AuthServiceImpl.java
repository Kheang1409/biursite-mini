package com.biursite.service.impl;

import com.biursite.domain.user.repository.UserRepositoryPort;
import com.biursite.dto.AuthRequest;
import com.biursite.dto.AuthResponse;
import com.biursite.dto.CreateUserRequest;
import com.biursite.dto.RegisterRequest;
import com.biursite.domain.user.entity.User;
import com.biursite.exception.BadRequestException;
import com.biursite.exception.UnauthorizedException;
import com.biursite.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.biursite.service.AuthService;
import com.biursite.application.user.usecase.CreateUserUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
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

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }
        
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token);
    }

    @Override
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
        User created = createUserUseCase.execute(cmd);

        String token = jwtUtil.generateToken(created.getUsername(), created.getRole().name());
        return new AuthResponse(token);
    }
}
