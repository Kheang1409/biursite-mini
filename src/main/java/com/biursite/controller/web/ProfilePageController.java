package com.biursite.controller.web;

import com.biursite.domain.post.repository.PostRepositoryPort;
import com.biursite.domain.user.repository.UserRepositoryPort;
import com.biursite.dto.UpdateUserRequest;
import com.biursite.domain.user.entity.User;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class ProfilePageController {
    private final UserRepositoryPort userRepository;
    private final PostRepositoryPort postRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfilePageController(UserRepositoryPort userRepository,
                                  PostRepositoryPort postRepository,
                                  PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/profile")
    public String profile(Model model, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        model.addAttribute("user", user);
        model.addAttribute("updateUserRequest", new UpdateUserRequest(user.getUsername(), user.getEmail(), null));
        model.addAttribute("userPosts", postRepository.findByAuthorOrderByCreatedAtDesc(user));
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute UpdateUserRequest updateUserRequest,
                                BindingResult result, Authentication auth, Model model) {
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("userPosts", postRepository.findByAuthorOrderByCreatedAtDesc(user));
            return "profile";
        }
        
        user.setUsername(updateUserRequest.getUsername());
        user.setEmail(updateUserRequest.getEmail());
        
        if (updateUserRequest.getPassword() != null && !updateUserRequest.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(updateUserRequest.getPassword()));
        }
        
        userRepository.save(user);
        model.addAttribute("user", user);
        model.addAttribute("updateUserRequest", new UpdateUserRequest(user.getUsername(), user.getEmail(), null));
        model.addAttribute("userPosts", postRepository.findByAuthorOrderByCreatedAtDesc(user));
        model.addAttribute("success", "Profile updated successfully");
        return "profile";
    }
}
