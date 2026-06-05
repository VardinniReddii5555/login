package com.emudhra.Registration.service;

import com.emudhra.Registration.model.UserOtp;
import com.emudhra.Registration.repository.UserOtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {
    @Autowired
    private UserOtpRepository otpRepository;
    public String generateOtp() {
        return String.valueOf(
                100000 + new Random().nextInt(999999)
        );
    }
    public UserOtp createOtp(
            String username,
            String email) {

        UserOtp userOtp = new UserOtp();

        userOtp.setUsername(username);
        userOtp.setEmail(email);
        userOtp.setOtp(generateOtp());

        userOtp.setCreatedAt(
                LocalDateTime.now()
        );

        userOtp.setExpiryAt(
                LocalDateTime.now().plusMinutes(5)
        );

        userOtp.setVerified(false);
        userOtp.setAttempts(0);

        return otpRepository.save(userOtp);
    }
}
