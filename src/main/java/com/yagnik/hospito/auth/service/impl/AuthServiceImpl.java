package com.yagnik.hospito.auth.service.impl;

import com.yagnik.hospito.auth.dto.AuthResponse;
import com.yagnik.hospito.auth.dto.LoginRequest;
import com.yagnik.hospito.auth.dto.RegisterRequest;
import com.yagnik.hospito.auth.entity.Role;
import com.yagnik.hospito.auth.entity.User;
import com.yagnik.hospito.auth.repository.RoleRepository;
import com.yagnik.hospito.auth.repository.UserRepository;
import com.yagnik.hospito.auth.security.JwtService;
import com.yagnik.hospito.auth.service.AuthService;
import com.yagnik.hospito.common.exception.BusinessRuleException;
import com.yagnik.hospito.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("Email already registered: " + request.getEmail());
        }

        // Find the role
        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Role not found: " + request.getRole() +
                                ". Make sure roles are seeded in the database."));

        // Create user with hashed password
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .isActive(true)
                .build();

        userRepository.save(user);

        // Generate token
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + role.getName().name())));

        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(role.getName())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // This will throw if credentials are wrong
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName().name())));

        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().getName())
                .build();
    }
}