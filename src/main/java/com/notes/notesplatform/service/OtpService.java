/*package com.notes.notesplatform.service;

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
}*/


package com.notes.notesplatform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    // Map stores Email -> OtpData (OTP + Expiry Timestamp)
    private final Map<String, OtpData> otpStorage = new ConcurrentHashMap<>();
    
    private static final long EXPIRE_MINUTES = 5;

    @Autowired
    private EmailService emailService;

    /**
     * Internal helper class to store OTP with its expiration time.
     */
    private static class OtpData {
        private final String otp;
        private final long expiryTimestamp;

        public OtpData(String otp, long durationInMinutes) {
            this.otp = otp;
            this.expiryTimestamp = System.currentTimeMillis() + (durationInMinutes * 60 * 1000);
        }

        public String getOtp() {
            return otp;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expiryTimestamp;
        }
    }

    public String generateOtp(String email) {
        // Generate a 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(1000000));
        
        // Wrap and store
        otpStorage.put(email, new OtpData(otp, EXPIRE_MINUTES));

        System.out.println("[OtpService] Generated OTP for " + email + ": " + otp + " (Expires in 5m)");

        try {
            emailService.sendOtpEmail(email, otp);
            System.out.println("[OtpService] OTP email sent successfully to " + email);
        } catch (Exception e) {
            System.err.println("[OtpService] Failed to send email: " + e.getMessage());
        }

        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        OtpData data = otpStorage.get(email);

        if (data == null) {
            System.out.println("[OtpService] No OTP record found for: " + email);
            return false;
        }

        if (data.isExpired()) {
            System.out.println("[OtpService] OTP has expired for: " + email);
            otpStorage.remove(email); // Remove expired entry
            return false;
        }

        boolean isValid = data.getOtp().equals(otp);
        
        if (isValid) {
            System.out.println("[OtpService] OTP Validated successfully for: " + email);
            // Optional: clear memory immediately after successful login
            otpStorage.remove(email); 
        } else {
            System.out.println("[OtpService] Invalid OTP attempt for: " + email);
        }

        return isValid;
    }

    public void clearOtp(String email) {
        otpStorage.remove(email);
        System.out.println("[OtpService] Manually cleared OTP for " + email);
    }

    /**
     * Background task: Runs every 10 minutes to remove "ghost" OTPs 
     * from users who never attempted to validate.
     */
    @Scheduled(fixedRate = 600000) 
    public void cleanUpExpiredOtps() {
        int initialSize = otpStorage.size();
        otpStorage.entrySet().removeIf(entry -> entry.getValue().isExpired());
        int removedCount = initialSize - otpStorage.size();
        
        if (removedCount > 0) {
            System.out.println("[OtpService] Cleanup Task: Removed " + removedCount + " expired OTPs from memory.");
        }
    }
}