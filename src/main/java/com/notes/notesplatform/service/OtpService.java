package com.notes.notesplatform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {

    private final Map<String, String> otpStorage = new HashMap<>();

    @Autowired
    private EmailService emailService;

    public String generateOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStorage.put(email, otp);

        System.out.println("[OtpService] Generated OTP for " + email + ": " + otp);

        try {
            emailService.sendOtpEmail(email, otp);
            System.out.println("[OtpService] OTP email sent successfully to " + email);
        } catch (Exception e) {
            System.out.println("[OtpService] Error sending OTP to " + email + ": " + e.getMessage());
            e.printStackTrace();
        }

        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        String storedOtp = otpStorage.get(email);
        System.out.println("[OtpService] Validating OTP for " + email + ". Stored: " + storedOtp + ", Received: " + otp);
        return storedOtp != null && storedOtp.equals(otp);
    }

    public void clearOtp(String email) {
        otpStorage.remove(email);
        System.out.println("[OtpService] Cleared OTP for " + email);
    }
}