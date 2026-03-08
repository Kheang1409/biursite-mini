package com.biursite.service.impl;

import com.biursite.dto.AuthRequest;
import com.biursite.dto.AuthResponse;
import com.biursite.dto.RegisterRequest;
import com.biursite.entity.Role;
import com.biursite.entity.User;
import com.biursite.repository.UserRepository;
import com.biursite.security.JwtUtil;
import com.biursite.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token);
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        // simple check
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username taken");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email taken");
        }
        User u = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .createdAt(Instant.now())
                .build();
        userRepository.save(u);
        String token = jwtUtil.generateToken(u.getUsername(), u.getRole().name());
        return new AuthResponse(token);
    }
}
