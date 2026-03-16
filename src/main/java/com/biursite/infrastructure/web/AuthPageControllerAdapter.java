package com.biursite.infrastructure.web;

import com.biursite.application.user.usecase.RegisterUserUseCase;
import com.biursite.application.user.dto.CreateUserCommand;
import com.biursite.infrastructure.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthPageControllerAdapter {
    private final RegisterUserUseCase registerUserUseCase;

    public AuthPageControllerAdapter(RegisterUserUseCase registerUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
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

        try {
            registerUserUseCase.execute(new CreateUserCommand(registerRequest.getUsername(), registerRequest.getEmail(), registerRequest.getPassword()));
            return "redirect:/login?registered";
        } catch (IllegalStateException ex) {
            String msg = ex.getMessage();
            if (msg != null && msg.contains("Username")) model.addAttribute("usernameError", msg);
            else if (msg != null && msg.contains("Email")) model.addAttribute("emailError", msg);
            else model.addAttribute("error", msg);
            return "register";
        }
    }
}
