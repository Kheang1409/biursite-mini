package com.biursite.infrastructure.web;

import com.biursite.infrastructure.web.dto.AuthRequest;
import com.biursite.infrastructure.web.dto.AuthResponse;
import com.biursite.infrastructure.web.dto.ApiResponse;
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
import jakarta.servlet.http.HttpServletRequest;

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
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest req, HttpServletRequest request) {
        String token = authenticateUserUseCase.execute(req.getUsername(), req.getPassword());
        var body = ApiResponse.success(200, "Authenticated", request.getRequestURI(), new AuthResponse(token));
        return ResponseEntity.ok(body);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest req, HttpServletRequest request) {
        registerUserUseCase.execute(new CreateUserCommand(req.getUsername(), req.getEmail(), req.getPassword()));
        String token = authenticateUserUseCase.execute(req.getUsername(), req.getPassword());
        var body = ApiResponse.success(200, "Registered", request.getRequestURI(), new AuthResponse(token));
        return ResponseEntity.ok(body);
    }
}
