package com.notes.notesplatform.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${spring.mail.username}")
    private String senderEmail;

    private static final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";

    public void sendOtpEmail(String to, String otp, String type) {
        RestTemplate restTemplate = new RestTemplate();

        // 1. Set Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoApiKey);

        // 2. Construct Request Body
        Map<String, Object> requestBody = new HashMap<>();

        // Sender Info
        Map<String, String> sender = new HashMap<>();
        sender.put("email", senderEmail);
        sender.put("name", "NotesPortal");

        // Recipient Info (Brevo expects an array/list of recipients)
        Map<String, String> recipient = new HashMap<>();
        recipient.put("email", to);

        String actionText;
    switch (type.toLowerCase()) {
        case "login":
            actionText = "complete your login";
            break;
        case "register":
            actionText = "verify your new account registration";
            break;
        case "reset":
            actionText = "reset your password";
            break;
        default:
            actionText = "verify your identity";
    }

        requestBody.put("sender", sender);
        requestBody.put("to", List.of(recipient)); 
        requestBody.put("subject", "Your OTP Code - NotesPortal");
       String htmlTemplate = 
        "<div style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>" +
            "<div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.1);'>" +
                "<div style='background-color: #2563eb; padding: 20px; text-align: center;'>" +
                    "<h1 style='color: #ffffff; margin: 0; font-size: 24px;'>NotesPortal</h1>" +
                "</div>" +
                "<div style='padding: 30px; text-align: center;'>" +
                    "<h2 style='color: #333333;'>Verification Code</h2>" +
                    // DYNAMIC TEXT USED HERE
                    "<p style='color: #666666; font-size: 16px;'>Please use the following One-Time Password (OTP) to <b>" + actionText + "</b>. This code is valid for 5 minutes.</p>" +
                    "<div style='margin: 30px 0;'>" +
                        "<span style='display: inline-block; background-color: #f3f4f6; color: #2563eb; font-size: 36px; font-weight: bold; letter-spacing: 5px; padding: 15px 30px; border-radius: 5px; border: 1px dashed #2563eb;'>" +
                            otp +
                        "</span>" +
                    "</div>" +
                    "<p style='color: #999999; font-size: 12px;'>If you did not request this code, please ignore this email or contact support.</p>" +
                "</div>" +
                "<div style='background-color: #f9fafb; padding: 15px; text-align: center; color: #9ca3af; font-size: 12px;'>" +
                    "&copy; 2026 NotesPortal. All rights reserved." +
                "</div>" +
            "</div>" +
        "</div>";
        requestBody.put("htmlContent", htmlTemplate);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(BREVO_URL, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("[EmailService] OTP sent successfully to " + to);
            } else {
                System.err.println("[EmailService] Failed to send email. Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("[EmailService] Brevo API Error: " + e.getMessage());
        }
    }
}