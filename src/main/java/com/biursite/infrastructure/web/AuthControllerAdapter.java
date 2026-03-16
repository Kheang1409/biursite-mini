package com.biursite.infrastructure.web;

import com.biursite.infrastructure.web.dto.AuthRequest;
import com.biursite.infrastructure.web.dto.AuthResponse;
import com.biursite.infrastructure.web.dto.RegisterRequest;
import com.biursite.application.user.usecase.AuthenticateUserUseCase;
import com.biursite.application.user.usecase.RegisterUserUseCase;
import com.biursite.application.user.dto.CreateUserCommand;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthControllerAdapter {
    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final RegisterUserUseCase registerUserUseCase;

    public AuthControllerAdapter(AuthenticateUserUseCase authenticateUserUseCase,
                                 RegisterUserUseCase registerUserUseCase) {
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.registerUserUseCase = registerUserUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req) {
        String token = authenticateUserUseCase.execute(req.getUsername(), req.getPassword());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        // register user then authenticate to issue token
        registerUserUseCase.execute(new CreateUserCommand(req.getUsername(), req.getEmail(), req.getPassword()));
        String token = authenticateUserUseCase.execute(req.getUsername(), req.getPassword());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
