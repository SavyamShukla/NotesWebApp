package com.notes.notesplatform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    // Using ConcurrentHashMap for better thread safety in a web environment
    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();

    @Autowired
    private EmailService emailService;

    public String generateOtp(String email) {
        // Generate a 6-digit random number
        String otp = String.format("%06d", new Random().nextInt(1000000));
        otpStorage.put(email, otp);

        System.out.println("[OtpService] Generated OTP for " + email + ": " + otp);

        // Send the email via the HTTP-based EmailService
        emailService.sendOtpEmail(email, otp);

        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        String storedOtp = otpStorage.get(email);
        boolean isValid = storedOtp != null && storedOtp.equals(otp);
        
        System.out.println("[OtpService] Validating OTP for " + email + ": " + (isValid ? "SUCCESS" : "FAILED"));
        return isValid;
    }

    public void clearOtp(String email) {
        otpStorage.remove(email);
        System.out.println("[OtpService] Cleared OTP for " + email);
    }
}