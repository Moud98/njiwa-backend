package com.wssoftwares.ikutibnb_backend.auth.service;

import com.wssoftwares.ikutibnb_backend.auth.dto.LoginRequest;
import com.wssoftwares.ikutibnb_backend.auth.dto.LoginResponse;
import com.wssoftwares.ikutibnb_backend.users.UserRepository;
import com.wssoftwares.ikutibnb_backend.users.entity.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class AuthService {
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthService(AuthenticationManager authManager, JwtService jwtService, UserRepository userRepository) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public LoginResponse loginUser(LoginRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtService.generateToken(user);

        return buildLoginResponse(user, token);
    }

    private LoginResponse buildLoginResponse(User user, String token) {

        String roleName = user.getRole().name();

        List<String> permissions = getPermissionsForRole(roleName);

        LoginResponse.Role role = new LoginResponse.Role(roleName, permissions);

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                user.getId(),
                user.getEmail(),
                role,
                user.getFullname(),
                user.getProfilepicture(),
                user.getPhonenumber());

        return new LoginResponse(token, userInfo);
    }

    private List<String> getPermissionsForRole(String roleName) {
        switch (roleName) {
            case "HOST":
                return Arrays.asList("CREAT_HOUSE", "EDIT_HOUSE", "VIEW_HOUSE", "VIEW_RENTREQUEST","ACCEPT_RENTREQUEST", "MANAGE_HOUSE");
            case "AGENT":
                return Arrays.asList("CREA_HOUSE", "VIEW_HOUSE","EDIT_HOUSE", "VIEW_RENTREQUEST") ;           
            case "ADMIN":
                return Arrays.asList("MANAGE_USERS", "CREATE_HOUSE", "DELETE_HOUSE", "CREATE_HOUSE", "CREATE_ADMINS");
            case "CLIENT":
                return Arrays.asList("VIEW_HOUSE", "VIEW_RENTREQUEST", "RENT_HOUSE");
            default:
                return Collections.emptyList();
        }
    }
}
