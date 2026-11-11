package com.wssoftwares.ikutibnb_backend.auth.service;

import com.wssoftwares.ikutibnb_backend.auth.entity.PasswordResetToken;
import com.wssoftwares.ikutibnb_backend.users.UserRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.wssoftwares.ikutibnb_backend.users.entity.User;
import com.wssoftwares.ikutibnb_backend.auth.repository.PasswordResetTokenRepository;

@Service
public class ForgotPasswordService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private OtpService otpService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordResetTokenRepository tokenRepo;

    public void requestOtp(String email) {
        userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        otpService.generateAndSendOtp(email);
    }

    public String verifyOtp(String otp) {
        return otpService.verifyAndReturnEmail(otp);
    }

    public void resetPassword(String email, String newPassword) {
        if (email == null || newPassword == null || email.isBlank() || newPassword.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid resetPassword credentials");
        }

        if (newPassword.length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password should be at least 8 characters");
        }

        PasswordResetToken token = tokenRepo.findByEmail(email)
                .filter(t -> t.isVerified() && t.getExpiresAt().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "No verified OTP found or OTP expired"));

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);
        userRepo.save(user);

        tokenRepo.delete(token);
    }

}