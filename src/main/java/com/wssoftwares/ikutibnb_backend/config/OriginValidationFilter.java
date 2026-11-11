package com.wssoftwares.ikutibnb_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

@Component
@Order(0) // Run before Spring Security
public class OriginValidationFilter extends OncePerRequestFilter {

    private static final Logger logger = Logger.getLogger(OriginValidationFilter.class.getName());

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();
        String origin = request.getHeader("Origin");

        // Allow CORS preflight requests (OPTIONS) to pass through
        if ("OPTIONS".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // For ALL endpoints, validate origin if Origin header is present
        if (origin != null && !origin.isEmpty()) {
            // Validate origin against allowed origins list
            boolean allowed = Arrays.stream(allowedOrigins.split(","))
                    .map(String::trim)
                    .anyMatch(origin::equalsIgnoreCase);

            if (!allowed) {
                logger.warning("CORS blocked - Origin not allowed: " + origin + " for " + method + " " + path);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Origin not allowed: " + origin);
                return;
            }
        }

        // Allow request through (origin is either allowed, or wasn't sent)
        filterChain.doFilter(request, response);
    }

}