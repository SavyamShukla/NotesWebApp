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

    public void sendOtpEmail(String to, String otp) {
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

        requestBody.put("sender", sender);
        requestBody.put("to", List.of(recipient)); 
        requestBody.put("subject", "Your OTP Code - NotesPortal");
        requestBody.put("htmlContent", 
            "<html><body>" +
            "<h2>Your OTP is: <span style='color:blue;'>" + otp + "</span></h2>" +
            "<p>This OTP is valid for 5 minutes. Do not share it with anyone.</p>" +
            "</body></html>"
        );

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