package com.wssoftwares.ikutibnb_backend.auth.controller;

import com.wssoftwares.ikutibnb_backend.auth.dto.LoginRequest;
import com.wssoftwares.ikutibnb_backend.auth.dto.LoginResponse;
import com.wssoftwares.ikutibnb_backend.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.loginUser(request);
            return ResponseEntity.ok(response);
        } catch (org.springframework.security.authentication.BadCredentialsException ex) {
            return ResponseEntity.status(401).body(Map.of("error", "Incorrect email or password"));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }

}
