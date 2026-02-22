package com.notes.notesplatform.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.Map;

@Service
public class StorageService {

        @Value("${supabase.s3.bucket}")
        private String bucketName;

        @Value("${supabase.s3.endpoint}")
        private String endpoint;

        @Value("${supabase.s3.region}")
        private String region;

        @Value("${supabase.s3.access-key}")
        private String accessKey;

        @Value("${supabase.s3.secret-key}")
        private String secretKey;

        @Value("${supabase.url}")
private String supabaseBaseUrl;

@Value("${supabase.service-role-key}")
private String serviceRoleKey;

        public String uploadFile(MultipartFile file) throws IOException {

                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

                S3Client s3Client = S3Client.builder()
                                .endpointOverride(URI.create(endpoint))
                                .region(Region.of(region))
                                .credentialsProvider(StaticCredentialsProvider.create(
                                                AwsBasicCredentials.create(accessKey, secretKey)))

                                .serviceConfiguration(S3Configuration.builder()
                                                .checksumValidationEnabled(false)
                                                .build())
                                .build();

                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                                .bucket(bucketName)
                                .key(fileName)
                                .contentType(file.getContentType())
                                .build();

                s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

                return fileName;
        }

        public String getSignedUrl(String fileName) {
    // 1. Define the Supabase Signing Endpoint
    String endpoint = supabaseBaseUrl + "/storage/v1/object/sign/" + bucketName + "/" + fileName;

    RestTemplate restTemplate = new RestTemplate();

    // 2. Set the Security Headers using the Service Role Key
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + serviceRoleKey);
    headers.setContentType(MediaType.APPLICATION_JSON);

    // 3. Set how long the link lasts (e.g., 15 minutes)
    Map<String, Object> body = Map.of("expiresIn", 900);
    HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

    try {
        // 4. Request the Signed URL
        ResponseEntity<Map> response = restTemplate.postForEntity(endpoint, request, Map.class);
        
        if (response.getStatusCode() == HttpStatus.OK) {
            String signedPath = (String) response.getBody().get("signedURL");
            // 5. Combine with the base URL to get the full "Security Link"
            return supabaseBaseUrl + signedPath;
        }
    } catch (Exception e) {
        System.err.println("Error generating signed URL: " + e.getMessage());
    }
    return null;
}
}