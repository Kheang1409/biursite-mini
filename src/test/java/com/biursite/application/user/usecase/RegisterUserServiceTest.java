package com.biursite.application.user.usecase;

import com.biursite.application.user.dto.CreateUserCommand;
import com.biursite.domain.user.entity.User;
import com.biursite.domain.user.repository.UserRepositoryPort;
import com.biursite.domain.user.service.PasswordHasher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserServiceTest {
    @Mock
    UserRepositoryPort userRepository;
    @Mock
    PasswordHasher passwordHasher;

    @InjectMocks
    RegisterUserService service;

    @Test
    void shouldRegisterUser() {
        when(userRepository.existsByUsername("u")).thenReturn(false);
        when(userRepository.existsByEmail("e@x.com")).thenReturn(false);
        when(passwordHasher.hash("p")).thenReturn("hp");

        User saved = User.builder().id(1L).username("u").email("e@x.com").password("hp").build();
        when(userRepository.save(any())).thenReturn(saved);

        CreateUserCommand cmd = new CreateUserCommand("u","e@x.com","p");
        User result = service.execute(cmd);

        assertNotNull(result);
        assertEquals("u", result.getUsername());
    }
}
