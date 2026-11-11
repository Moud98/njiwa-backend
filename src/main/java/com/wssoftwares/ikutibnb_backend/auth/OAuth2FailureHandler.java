package com.wssoftwares.ikutibnb_backend.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    private static final String FRONTEND_URL = "https://njiwaapp.com";
    private static final String MOBILE_URL = "njiwaapp://auth/callback";

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {

        String errorMessage = URLEncoder.encode(
                exception.getMessage(),
                StandardCharsets.UTF_8
        );

        boolean mobileLogin = request.getRequestURI().endsWith("/google-mobile");
        String redirectUrl = mobileLogin
                ? MOBILE_URL + "?error=" + errorMessage
                : FRONTEND_URL + "/auth/google/error?message=" + errorMessage;

        response.sendRedirect(redirectUrl);
    }
}
