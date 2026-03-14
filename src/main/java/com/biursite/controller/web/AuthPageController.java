package com.biursite.controller.web;

import com.biursite.domain.user.entity.User;
import com.biursite.domain.user.repository.UserRepositoryPort;
import com.biursite.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.Instant;

import com.biursite.domain.user.entity.Role;

@Controller
public class AuthPageController {
    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthPageController(UserRepositoryPort userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterRequest registerRequest,
                           BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            model.addAttribute("usernameError", "Username is already taken");
            return "register";
        }
        
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            model.addAttribute("emailError", "Email is already registered");
            return "register";
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.ROLE_USER)
                .createdAt(Instant.now())
                .build();
        userRepository.save(user);

        return "redirect:/login?registered";
    }
}
