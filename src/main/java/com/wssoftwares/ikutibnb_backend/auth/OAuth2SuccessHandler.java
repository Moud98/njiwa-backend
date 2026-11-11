package com.wssoftwares.ikutibnb_backend.auth;

import com.wssoftwares.ikutibnb_backend.auth.service.JwtService;
import com.wssoftwares.ikutibnb_backend.users.UserRepository;
import com.wssoftwares.ikutibnb_backend.users.entity.Role;
import com.wssoftwares.ikutibnb_backend.users.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    // Your frontend URL
    private static final String FRONTEND_URL = "https://njiwaapp.com";

    public OAuth2SuccessHandler(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");

        // Find or create user
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFullname(name != null ? name : email.split("@")[0]);
            newUser.setProfilepicture(picture);
            newUser.setRole(Role.CLIENT);
            return userRepository.save(newUser);
        });

        // Generate JWT token
        String token = jwtService.generateToken(user);

        String redirectUrl = buildRedirectUrl(token, user);
       
        response.sendRedirect(redirectUrl);
    }

    private String buildRedirectUrl(String token, User user) {
        StringBuilder url = new StringBuilder(FRONTEND_URL);

        url.append("/auth/google/callback");

        url.append("?accessToken=").append(URLEncoder.encode(token, StandardCharsets.UTF_8));
        url.append("&id=").append(user.getId());
        url.append("&email=").append(URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8));
        url.append("&fullname=").append(URLEncoder.encode(user.getFullname(), StandardCharsets.UTF_8));
        url.append("&role=").append(URLEncoder.encode(user.getRole().name(), StandardCharsets.UTF_8));
        
        if (user.getProfilepicture() != null && !user.getProfilepicture().isEmpty()) {
            url.append("&profilepicture=").append(URLEncoder.encode(user.getProfilepicture(), StandardCharsets.UTF_8));
        }
        
        if (user.getPhonenumber() != null && !user.getPhonenumber().isEmpty()) {
            url.append("&phonenumber=").append(URLEncoder.encode(user.getPhonenumber(), StandardCharsets.UTF_8));
        }
        
        return url.toString();
    }
}