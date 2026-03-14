package com.biursite.controller;

import com.biursite.application.user.usecase.CreateUserUseCase;
import com.biursite.application.user.usecase.GetUserByIdUseCase;
import com.biursite.application.user.usecase.GetAllUsersUseCase;
import com.biursite.application.user.usecase.UpdateUserUseCase;
import com.biursite.application.user.usecase.BanUnbanDeleteUserUseCases;
import com.biursite.security.SecurityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import com.biursite.security.JwtUtil;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private CreateUserUseCase createUserUseCase;
    @MockBean
    private GetUserByIdUseCase getUserByIdUseCase;
    @MockBean
    private GetAllUsersUseCase getAllUsersUseCase;
    @MockBean
    private UpdateUserUseCase updateUserUseCase;
    @MockBean
    private BanUnbanDeleteUserUseCases banUnbanDeleteUserUseCases;
    @MockBean
    private SecurityService securityService;
    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void getById_unauthenticated_returns403or401() throws Exception {
        mvc.perform(get("/api/users/1")).andExpect(status().is4xxClientError());
    }
}
