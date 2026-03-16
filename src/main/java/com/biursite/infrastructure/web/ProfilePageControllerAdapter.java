package com.biursite.infrastructure.web;

import com.biursite.application.user.dto.UpdateUserRequest;
import com.biursite.application.user.usecase.GetUserProfilePostsUseCase;
import com.biursite.application.user.usecase.UpdateUserUseCase;
import com.biursite.application.shared.security.CurrentUserPort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;

@Controller
public class ProfilePageControllerAdapter {
    private final GetUserProfilePostsUseCase getUserProfilePostsUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final CurrentUserPort currentUserPort;

    public ProfilePageControllerAdapter(GetUserProfilePostsUseCase getUserProfilePostsUseCase,
                                        UpdateUserUseCase updateUserUseCase,
                                        CurrentUserPort currentUserPort) {
        this.getUserProfilePostsUseCase = getUserProfilePostsUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.currentUserPort = currentUserPort;
    }

    @GetMapping("/profile")
    public String profile(Model model, java.security.Principal auth) {
        var view = getUserProfilePostsUseCase.execute(currentUserPort.getCurrentUserId(), com.biursite.application.shared.pagination.PageRequest.of(0, 100));
        com.biursite.application.user.dto.UpdateUserRequest req = new com.biursite.application.user.dto.UpdateUserRequest();
        req.setUsername(view.getUsername());
        req.setEmail("");
        model.addAttribute("user", view);
        model.addAttribute("updateUserRequest", req);
        model.addAttribute("userPosts", view.getPosts());
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute UpdateUserRequest updateUserRequest,
                                BindingResult result, java.security.Principal auth, Model model) {
        if (result.hasErrors()) {
            var view = getUserProfilePostsUseCase.execute(currentUserPort.getCurrentUserId(), com.biursite.application.shared.pagination.PageRequest.of(0, 100));
            model.addAttribute("user", view);
            model.addAttribute("userPosts", view.getPosts());
            return "profile";
        }

        var updated = updateUserUseCase.execute(currentUserPort.getCurrentUserId(), updateUserRequest);
        model.addAttribute("user", updated);
        var updateReq2 = new UpdateUserRequest();
        updateReq2.setUsername(updated.getUsername());
        updateReq2.setEmail(updated.getEmail());
        updateReq2.setPassword(null);
        model.addAttribute("updateUserRequest", updateReq2);
        var posts = getUserProfilePostsUseCase.execute(updated.getId(), com.biursite.application.shared.pagination.PageRequest.of(0, 100));
        model.addAttribute("userPosts", posts.getPosts());
        model.addAttribute("success", "Profile updated successfully");
        return "profile";
    }
}
