package com.biursite.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void registerAndLoginFlow_withCsrf() throws Exception {
        // GET register page contains csrf
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("_csrf")));

        // Perform registration (include email and csrf)
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "testuser")
                        .param("email", "testuser@example.com")
                        .param("password", "Password123!"))
                .andExpect(status().is3xxRedirection());

        // GET login page -> has csrf
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("_csrf")));

        // Attempt login with credentials and keep session
        var loginResult = mockMvc.perform(post("/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "testuser")
                        .param("password", "Password123!"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        var session = loginResult.getRequest().getSession(false);

        // Access a protected page to ensure session works (profile)
        mockMvc.perform(get("/profile").session((org.springframework.mock.web.MockHttpSession) session))
                .andExpect(status().isOk());

        // Logout should be present and accept POST (with csrf and session)
        mockMvc.perform(post("/logout").with(csrf()).session((org.springframework.mock.web.MockHttpSession) session))
                .andExpect(status().is3xxRedirection());
    }
}
