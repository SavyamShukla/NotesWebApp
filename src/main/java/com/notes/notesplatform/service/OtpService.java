package com.notes.notesplatform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private final Map<String, OtpData> otpStorage = new ConcurrentHashMap<>();

    private static final long EXPIRE_MINUTES = 5;
    private static final long COOLDOWN_SECONDS = 60;

    @Autowired
    private EmailService emailService;

    private static class OtpData {
        private final String otp;
        private final long expiryTimestamp;
        private final long lastSentTimestamp;

        public OtpData(String otp, long durationInMinutes) {
            this.otp = otp;
            this.lastSentTimestamp = System.currentTimeMillis();
            this.expiryTimestamp = System.currentTimeMillis() + (durationInMinutes * 60 * 1000);
        }

        public String getOtp() {
            return otp;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expiryTimestamp;
        }

        public long getSecondsSinceLastSent() {
            return (System.currentTimeMillis() - lastSentTimestamp) / 1000;
        }
    }

    public String generateOtp(String email, String type) {

        OtpData existingData = otpStorage.get(email);
        if (existingData != null && existingData.getSecondsSinceLastSent() < COOLDOWN_SECONDS) {
            long waitTime = COOLDOWN_SECONDS - existingData.getSecondsSinceLastSent();
            throw new RuntimeException("Please wait " + waitTime + " seconds before requesting a new OTP.");
        }

        String otp = String.format("%06d", new Random().nextInt(1000000));
        otpStorage.put(email, new OtpData(otp, EXPIRE_MINUTES));

        System.out.println("[OtpService] OTP generated for " + email + ": " + otp);

        try {
            emailService.sendOtpEmail(email, otp, type);
        } catch (Exception e) {
            System.err.println("[OtpService] Email error: " + e.getMessage());
        }

        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        OtpData data = otpStorage.get(email);

        if (data == null || data.isExpired()) {
            if (data != null)
                otpStorage.remove(email);
            return false;
        }

        boolean isValid = data.getOtp().equals(otp);
        if (isValid)
            otpStorage.remove(email);

        return isValid;
    }

    public void clearOtp(String email) {
        otpStorage.remove(email);
        System.out.println("[OtpService] Manually cleared OTP for " + email);
    }

    @Scheduled(fixedRate = 600000)
    public void cleanUpExpiredOtps() {
        otpStorage.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
}