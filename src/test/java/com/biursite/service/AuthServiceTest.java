package com.biursite.service;

import com.biursite.domain.user.repository.UserRepositoryPort;
import com.biursite.infrastructure.web.dto.*;
import com.biursite.domain.user.entity.Role;
import com.biursite.domain.user.entity.User;
import com.biursite.application.shared.exception.BadRequestException;
import com.biursite.application.shared.exception.UnauthorizedException;
import com.biursite.infrastructure.security.JwtUtil;
import com.biursite.infrastructure.service.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepositoryPort userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private com.biursite.application.user.usecase.CreateUserUseCase createUserUseCase;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("alice")
                .email("alice@test.com")
                .password("encoded_pass")
                .role(Role.ROLE_USER)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void login_validCredentials_returnsToken() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("rawpass", "encoded_pass")).thenReturn(true);
        when(jwtUtil.generateToken("alice", "ROLE_USER")).thenReturn("jwt-token");

        AuthResponse response = authService.login(new AuthRequest("alice", "rawpass"));
        assertThat(response.getToken()).isEqualTo("jwt-token");
    }

    @Test
    void login_userNotFound_throwsUnauthorized() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> authService.login(new AuthRequest("nonexistent", "pass")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid credentials");
    }

    @Test
    void login_wrongPassword_throwsUnauthorized() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrong", "encoded_pass")).thenReturn(false);
        assertThatThrownBy(() -> authService.login(new AuthRequest("alice", "wrong")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid credentials");
    }

    @Test
    void register_newUser_returnsToken() {
        when(userRepository.existsByUsername("bob")).thenReturn(false);
        when(userRepository.existsByEmail("bob@test.com")).thenReturn(false);
        com.biursite.application.user.dto.UserDto createdUser = com.biursite.application.user.dto.UserDto.builder()
            .id(2L).username("bob").email("bob@test.com").role("ROLE_USER").build();
        when(createUserUseCase.execute(any(com.biursite.application.user.dto.CreateUserCommand.class))).thenReturn(createdUser);
        when(jwtUtil.generateToken("bob", "ROLE_USER")).thenReturn("jwt-token-bob");

        RegisterRequest req = new RegisterRequest("bob", "bob@test.com", "pass123");
        AuthResponse response = authService.register(req);

        assertThat(response.getToken()).isEqualTo("jwt-token-bob");
        verify(createUserUseCase).execute(any(com.biursite.application.user.dto.CreateUserCommand.class));
    }

    @Test
    void register_duplicateUsername_throwsBadRequest() {
        when(userRepository.existsByUsername("alice")).thenReturn(true);
        RegisterRequest req = new RegisterRequest("alice", "new@test.com", "pass");
        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Username taken");
    }

    @Test
    void register_duplicateEmail_throwsBadRequest() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("alice@test.com")).thenReturn(true);
        RegisterRequest req = new RegisterRequest("newuser", "alice@test.com", "pass");
        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email taken");
    }
}
