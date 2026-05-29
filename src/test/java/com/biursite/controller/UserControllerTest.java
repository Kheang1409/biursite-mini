package com.biursite.controller;

import com.biursite.application.user.usecase.CreateUserUseCase;
import com.biursite.application.query.GetUserQuery;
import com.biursite.application.query.GetUserPageQuery;
import com.biursite.application.user.usecase.UpdateUserUseCase;
import com.biursite.application.user.usecase.BanUnbanDeleteUserUseCases;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import com.biursite.infrastructure.security.JwtUtil;
import com.biursite.infrastructure.web.UserControllerAdapter;
import org.mockito.Mockito;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserControllerAdapter.class)
@Import(UserControllerTest.MockConfig.class)
class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    @TestConfiguration
    static class MockConfig {
        @Bean
        CreateUserUseCase createUserUseCase() {
            return Mockito.mock(CreateUserUseCase.class);
        }

        @Bean
        GetUserQuery getUserQuery() {
            return Mockito.mock(GetUserQuery.class);
        }

        @Bean
        GetUserPageQuery getUserPageQuery() {
            return Mockito.mock(GetUserPageQuery.class);
        }

        @Bean
        UpdateUserUseCase updateUserUseCase() {
            return Mockito.mock(UpdateUserUseCase.class);
        }

        @Bean
        BanUnbanDeleteUserUseCases banUnbanDeleteUserUseCases() {
            return Mockito.mock(BanUnbanDeleteUserUseCases.class);
        }

        @Bean
        com.biursite.application.shared.security.CurrentUserPort currentUserPort() {
            return Mockito.mock(com.biursite.application.shared.security.CurrentUserPort.class);
        }

        @Bean
        JwtUtil jwtUtil() {
            return Mockito.mock(JwtUtil.class);
        }
    }

    @Test
    void getById_unauthenticated_returns403or401() throws Exception {
        mvc.perform(get("/api/users/1")).andExpect(status().is4xxClientError());
    }
}
