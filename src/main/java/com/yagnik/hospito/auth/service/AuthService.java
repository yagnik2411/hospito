package com.yagnik.hospito.auth.service;

import com.yagnik.hospito.auth.dto.AuthResponse;
import com.yagnik.hospito.auth.dto.LoginRequest;
import com.yagnik.hospito.auth.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}