package com.notes.notesplatform.controller;

import com.notes.notesplatform.model.User;
import com.notes.notesplatform.repository.UserRepository;
import com.notes.notesplatform.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class RegisterController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

   /* @PostMapping("/register/send-otp")
    @ResponseBody
    public String sendOtp(@RequestParam("email") String email) {
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            return "exists";
        }
        otpService.generateOtp(email);
        return "sent";
    }*/

        @PostMapping("/register/send-otp")
@ResponseBody
public ResponseEntity<String> sendOtp(@RequestParam("email") String email) {
    Optional<User> existing = userRepository.findByEmail(email);
    if (existing.isPresent()) {
        return ResponseEntity.ok("exists");
    }
    
    try {
        otpService.generateOtp(email, "register");
        return ResponseEntity.ok("sent");
    } catch (RuntimeException e) {
        // Returns the cooldown message to the frontend
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(e.getMessage());
    }
}

    @PostMapping("/register/verify-otp")
    @ResponseBody
    public String verifyOtp(@RequestParam String email, @RequestParam String otp) {
        if (otpService.validateOtp(email, otp)) {
            otpService.clearOtp(email); // clear OTP after successful validation
            return "verified";
        }
        return "invalid";
    }

    @PostMapping("/register/complete")
    @ResponseBody
    public String completeRegistration(@RequestParam("email") String email,
                                       @RequestParam("name") String name,
                                       @RequestParam("password") String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            return "exists";
        }

        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        return "success";
    }
}