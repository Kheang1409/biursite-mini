package com.biursite.service;

import com.biursite.dto.AuthRequest;
import com.biursite.dto.AuthResponse;
import com.biursite.dto.RegisterRequest;

public interface AuthService {
    AuthResponse login(AuthRequest request);
    AuthResponse register(RegisterRequest request);
}
