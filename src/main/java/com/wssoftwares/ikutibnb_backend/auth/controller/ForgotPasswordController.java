package com.wssoftwares.ikutibnb_backend.auth.controller;

import com.wssoftwares.ikutibnb_backend.auth.service.*;
import com.wssoftwares.ikutibnb_backend.auth.dto.ForgotPasswordRequest;
import com.wssoftwares.ikutibnb_backend.auth.dto.VerifyOtpRequest;
import com.wssoftwares.ikutibnb_backend.auth.dto.ResetPasswordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class ForgotPasswordController {

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @PostMapping("/forgot-password")
    public String requestOtp(@RequestBody ForgotPasswordRequest request) {
        forgotPasswordService.requestOtp(request.getEmail());
        return "OTP has been sent to your email.";
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request) {
        String email = forgotPasswordService.verifyOtp(request.getOtp());
        if (email != null) {
            return ResponseEntity.ok(Map.of("message", "OTP verified", "email", email));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Invalid or expired OTP"));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        forgotPasswordService.resetPassword(request.getEmail(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }

}