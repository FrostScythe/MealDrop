package com.restaurantmanagement.order_api.controller;

import com.restaurantmanagement.order_api.dto.request.LoginRequest;
import com.restaurantmanagement.order_api.dto.request.UserRegisterRequest;
import com.restaurantmanagement.order_api.dto.response.AuthResponse;
import com.restaurantmanagement.order_api.entity.Role;
import com.restaurantmanagement.order_api.entity.User;
import com.restaurantmanagement.order_api.repository.UserRepository;
import com.restaurantmanagement.order_api.security.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthenticationManager authManager;
    @Autowired private JwtUtils jwtUtils;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        // 1. This line does all the work:
        //    - loads user via CustomUserDetailsService
        //    - verifies password via BCrypt
        //    - throws BadCredentialsException if wrong
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        // 2. The principal is your User entity (since User implements UserDetails)
        User user = (User) auth.getPrincipal();

        // 3. Generate JWT
        String token = jwtUtils.generateToken(user);

        return ResponseEntity.ok(
                new AuthResponse(token, user.getRole().name(), user.getName()));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody UserRegisterRequest request) {

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setRole(Role.CUSTOMER);
        // Hash the password before saving — NEVER store plain text
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        String token = jwtUtils.generateToken(user);
        return ResponseEntity.ok(
                new AuthResponse(token, user.getRole().name(), user.getName()));
    }
}