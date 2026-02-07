package com.notes.notesplatform.model;

public class AuthRequest {
    private String username;  
    private String password;  
    private String otp;       

    public AuthRequest() {}

    public AuthRequest(String username, String password, String otp) {
        this.username = username;
        this.password = password;
        this.otp = otp;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
}