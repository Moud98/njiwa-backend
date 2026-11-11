package com.wssoftwares.ikutibnb_backend.auth.controller;

import com.wssoftwares.ikutibnb_backend.auth.service.JwtService;
import com.wssoftwares.ikutibnb_backend.users.UserRepository;
import com.wssoftwares.ikutibnb_backend.users.entity.Role;
import com.wssoftwares.ikutibnb_backend.users.entity.User;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class OAuth2Controller {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public OAuth2Controller(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @GetMapping("/success")
    public ResponseEntity<?> oauth2Success(@RequestParam("accessToken") String token) {
        Claims claims = jwtService.parseToken(token);
        String email = claims.getSubject();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("email", user.getEmail());
        userData.put("role", user.getRole().name());
        userData.put("fullname", user.getFullname());
        userData.put("profilepicture", user.getProfilepicture());
        userData.put("phonenumber", user.getPhonenumber());

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", token);
        response.put("user", userData);

        return ResponseEntity.ok(response);
    }

}
