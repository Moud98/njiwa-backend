package com.wssoftwares.ikutibnb_backend.auth.service;

import com.wssoftwares.ikutibnb_backend.auth.entity.PasswordResetToken;
import com.wssoftwares.ikutibnb_backend.auth.repository.PasswordResetTokenRepository;
import com.wssoftwares.ikutibnb_backend.auth.util.OtpGenerator;
import com.wssoftwares.ikutibnb_backend.auth.util.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OtpService {

    @Autowired
    private PasswordResetTokenRepository tokenRepo;

    @Autowired
    private EmailSender emailSender;

    public void generateAndSendOtp(String email) {
        String otp = OtpGenerator.generateOtp();
        PasswordResetToken token = new PasswordResetToken();
        token.setEmail(email);
        token.setOtp(otp);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        token.setVerified(false);

        tokenRepo.deleteByEmail(email);
        tokenRepo.save(token);
        emailSender.sendOtpEmail(email, otp);
    }

   public String verifyAndReturnEmail(String otp) {
    return tokenRepo.findByOtp(otp)
            .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))
            .map(t -> {
                t.setVerified(true);
                tokenRepo.save(t);
                return t.getEmail();
            })
            .orElse(null);
}


    public String getVerifiedEmailByOtp(String otp) {
        return tokenRepo.findByOtp(otp)
                .filter(PasswordResetToken::isVerified)
                .map(PasswordResetToken::getEmail)
                .orElse(null);
    }

    @Transactional
    public void invalidateOtp(String email) {
        tokenRepo.deleteByEmail(email);
    }
}
