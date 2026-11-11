package com.wssoftwares.ikutibnb_backend.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class OAuth2CompositeSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2SuccessHandler webHandler;
    private final OAuth2MobileSuccessHandler mobileHandler;

    public OAuth2CompositeSuccessHandler(OAuth2SuccessHandler webHandler, OAuth2MobileSuccessHandler mobileHandler) {
        this.webHandler = webHandler;
        this.mobileHandler = mobileHandler;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {

        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
            String clientRegistrationId = token.getAuthorizedClientRegistrationId();

            // Route to mobile handler for mobile registrations
            if ("google-mobile".equals(clientRegistrationId)) {
                mobileHandler.onAuthenticationSuccess(request, response, authentication);
            } else {
                // Route to web handler (default for 'google')
                webHandler.onAuthenticationSuccess(request, response, authentication);
            }
        }
    }
}
