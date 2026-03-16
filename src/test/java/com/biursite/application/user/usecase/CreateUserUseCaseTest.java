package com.biursite.application.user.usecase;

import com.biursite.application.user.dto.CreateUserCommand;
import com.biursite.domain.shared.event.DomainEventPublisher;
import com.biursite.application.user.dto.UserDto;
import com.biursite.domain.user.entity.User;
import com.biursite.domain.user.entity.Role;
import com.biursite.domain.user.repository.UserRepositoryPort;
import com.biursite.domain.user.service.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import com.biursite.domain.shared.event.DomainEvent;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateUserUseCaseTest {
    private UserRepositoryPort userRepository;
    private PasswordHasher passwordHasher;
    private DomainEventPublisher eventPublisher;
    private CreateUserUseCase useCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepositoryPort.class);
        passwordHasher = mock(PasswordHasher.class);
        eventPublisher = mock(DomainEventPublisher.class);
        useCase = new CreateUserUseCase(userRepository, passwordHasher, eventPublisher);
    }

    @Test
    void createsUser_and_publishesEvent() {
        when(passwordHasher.hash("secret")).thenReturn("hashed");

        User saved = User.builder().id(1L).username("u").email("e@x.com").password("hashed").role(Role.ROLE_USER).build();
        when(userRepository.save(any())).thenReturn(saved);

        CreateUserCommand cmd = new CreateUserCommand("u", "e@x.com", "secret");
        UserDto result = useCase.execute(cmd);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(passwordHasher).hash("secret");

        ArgumentCaptor<DomainEvent> eventCaptor = ArgumentCaptor.forClass(DomainEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());
        assertTrue(eventCaptor.getValue() instanceof com.biursite.domain.user.event.UserRegisteredEvent);
    }
}
