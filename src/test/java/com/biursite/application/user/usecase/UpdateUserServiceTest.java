package com.biursite.application.user.usecase;

import com.biursite.application.user.dto.UpdateUserRequest;
import com.biursite.application.user.mapper.UserDtoMapper;
import com.biursite.domain.user.entity.User;
import com.biursite.domain.user.repository.UserRepositoryPort;
import com.biursite.domain.user.service.PasswordHasher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserServiceTest {
    @Mock
    UserRepositoryPort userRepository;
    @Mock
    PasswordHasher passwordHasher;

    @Mock
    UserDtoMapper userDtoMapper;

    @InjectMocks
    UpdateUserService service;

    @Test
    void shouldUpdateUser() {
        User u = User.builder().id(2L).username("old").email("old@x").build();
        when(userRepository.findById(2L)).thenReturn(Optional.of(u));
        when(passwordHasher.hash("np")).thenReturn("hp");
        when(userRepository.save(any())).thenReturn(u);
        when(userDtoMapper.toDto(org.mockito.ArgumentMatchers.any())).thenReturn(new com.biursite.application.user.dto.UserDto(u.getId(), "new", "new@x", null, false, false, u.getCreatedAt()));

        UpdateUserRequest req = new UpdateUserRequest();
        req.setUsername("new");
        req.setEmail("new@x");
        req.setPassword("np");
        var res = service.execute(2L, req);

        assertNotNull(res);
        assertEquals("new", res.getUsername());
    }
}
