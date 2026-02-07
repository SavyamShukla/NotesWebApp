/*
package com.notes.notesplatform.controller;

import com.notes.notesplatform.model.User;
import com.notes.notesplatform.repository.UserRepository;
import com.notes.notesplatform.service.OtpService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.util.List;
import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OtpService otpService;

    @Autowired
    private HttpServletRequest request;

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "email", required = false) String email,
                                @RequestParam(value = "useOtp", required = false) Boolean useOtp,
                                Model model) {
        model.addAttribute("email", email);
        model.addAttribute("useOtp", useOtp != null && useOtp);
        return "login";
    }

    @PostMapping("/login-with-password")
    public String loginWithPassword(@RequestParam("email") String email,
                                    @RequestParam("password") String password,
                                    Model model) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPassword())) {
            model.addAttribute("error", "Invalid email or password.");
            model.addAttribute("email", email);
            model.addAttribute("useOtp", false);
            return "login";
        }

        User user = userOpt.get();
        authenticateUser(user);
        return "redirect:/index";
    }

    @PostMapping("/send-login-otp")
    public String sendLoginOtp(@RequestParam("email") String email, Model model) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            model.addAttribute("error", "User not found. Please register first.");
            model.addAttribute("email", email);
            model.addAttribute("useOtp", true);
            return "login";
        }

        otpService.generateOtp(email);
        model.addAttribute("message", "OTP has been sent to your email.");
        model.addAttribute("email", email);
        model.addAttribute("useOtp", true); 
        return "login";
    }

    @PostMapping("/login-with-otp")
    public String loginWithOtp(@RequestParam("email") String email,
                               @RequestParam("otp") String otp,
                               Model model) {

        if (!otpService.validateOtp(email, otp)) {
            model.addAttribute("error", "Invalid or expired OTP.");
            model.addAttribute("email", email);
            model.addAttribute("useOtp", true);
            return "login";
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "User not found. Please register first.");
            model.addAttribute("useOtp", true);
            return "login";
        }

        User user = userOpt.get();
        authenticateUser(user);
        otpService.clearOtp(email);

        return "redirect:/index";
    }

    private void authenticateUser(User user) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );

        
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        
        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", context);
    }

    
     @PostMapping("/forgot-password/send-otp")
public String sendForgotPasswordOtp(@RequestParam("email") String email, Model model) {
    Optional<User> userOpt = userRepository.findByEmail(email);

    if (userOpt.isEmpty()) {
        model.addAttribute("error", "User not found. Please register first.");
        return "login";
    }

    otpService.generateOtp(email);
    model.addAttribute("forgotPasswordMode", true);
    model.addAttribute("email", email);
    model.addAttribute("message", "OTP sent to your email. Enter OTP and new password.");
    return "login";
} 

@PostMapping("/forgot-password/reset")
public String resetPassword(@RequestParam("email") String email,
                            @RequestParam("otp") String otp,
                            @RequestParam("newPassword") String newPassword,
                            Model model) {

    
    if (!otpService.validateOtp(email, otp)) {
        String message = "Invalid or expired OTP for " + email + ". Please try again.";
        return "redirect:/reset-result?success=false&message=" + 
               URLEncoder.encode(message, StandardCharsets.UTF_8);
    }

    
    Optional<User> userOpt = userRepository.findByEmail(email);
    if (userOpt.isEmpty()) {
        String message = "User with email " + email + " not found.";
        return "redirect:/reset-result?success=false&message=" + 
               URLEncoder.encode(message, StandardCharsets.UTF_8);
    }

    
    User user = userOpt.get();
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
    otpService.clearOtp(email);

    String message = "Password for " + email + " updated successfully. You can now login with your new password.";
    return "redirect:/reset-result?success=true&message=" + 
           URLEncoder.encode(message, StandardCharsets.UTF_8);
}

@GetMapping("/reset-result")
public String showResetResult(@RequestParam("message") String message,
                              @RequestParam("success") boolean success,
                              Model model) {
    model.addAttribute("message", message);
    model.addAttribute("success", success);
    return "reset-result";
}
}*/

package com.notes.notesplatform.controller;

import com.notes.notesplatform.model.User;
import com.notes.notesplatform.repository.UserRepository;
import com.notes.notesplatform.service.JwtService;
import com.notes.notesplatform.service.OtpService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private OtpService otpService;

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "email", required = false) String email,
                                @RequestParam(value = "useOtp", required = false) Boolean useOtp,
                                Model model) {
        model.addAttribute("email", email);
        model.addAttribute("useOtp", useOtp != null && useOtp);
        return "login";
    }

    @PostMapping("/login-with-password")
    public String loginWithPassword(@RequestParam("email") String email,
                                    @RequestParam("password") String password,
                                    HttpServletResponse response,
                                    Model model) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPassword())) {
            model.addAttribute("error", "Invalid email or password.");
            return "login";
        }

        issueToken(email, response);
        return "redirect:/index";
    }

    @PostMapping("/login-with-otp")
    public String loginWithOtp(@RequestParam("email") String email,
                               @RequestParam("otp") String otp,
                               HttpServletResponse response,
                               Model model) {

        if (!otpService.validateOtp(email, otp)) {
            model.addAttribute("error", "Invalid or expired OTP.");
            model.addAttribute("email", email);
            model.addAttribute("useOtp", true);
            return "login";
        }

        issueToken(email, response);
        otpService.clearOtp(email);
        return "redirect:/index";
    }

    // Helper method to create the cookie
    private void issueToken(String email, HttpServletResponse response) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        String token = jwtService.generateToken(userDetails);

        Cookie jwtCookie = new Cookie("jwt", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false); // Set to true if using HTTPS on Render
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(3600 * 10); // 10 hours
        response.addCookie(jwtCookie);
    }

  /* @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("jwt", null);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0); // Deletes the cookie
        response.addCookie(jwtCookie);
        SecurityContextHolder.clearContext();
        return "redirect:/index";
    }*/


     @PostMapping("/forgot-password/send-otp")
public String sendForgotPasswordOtp(@RequestParam("email") String email, Model model) {
    Optional<User> userOpt = userRepository.findByEmail(email);

    if (userOpt.isEmpty()) {
        model.addAttribute("error", "User not found. Please register first.");
        return "login";
    }

    otpService.generateOtp(email);
    model.addAttribute("forgotPasswordMode", true);
    model.addAttribute("email", email);
    model.addAttribute("message", "OTP sent to your email. Enter OTP and new password.");
    return "login";
} 

@PostMapping("/forgot-password/reset")
public String resetPassword(@RequestParam("email") String email,
                            @RequestParam("otp") String otp,
                            @RequestParam("newPassword") String newPassword,
                            Model model) {

    
    if (!otpService.validateOtp(email, otp)) {
        String message = "Invalid or expired OTP for " + email + ". Please try again.";
        return "redirect:/reset-result?success=false&message=" + 
               URLEncoder.encode(message, StandardCharsets.UTF_8);
    }

    
    Optional<User> userOpt = userRepository.findByEmail(email);
    if (userOpt.isEmpty()) {
        String message = "User with email " + email + " not found.";
        return "redirect:/reset-result?success=false&message=" + 
               URLEncoder.encode(message, StandardCharsets.UTF_8);
    }

    
    User user = userOpt.get();
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
    otpService.clearOtp(email);

    String message = "Password for " + email + " updated successfully. You can now login with your new password.";
    return "redirect:/reset-result?success=true&message=" + 
           URLEncoder.encode(message, StandardCharsets.UTF_8);
}

@GetMapping("/reset-result")
public String showResetResult(@RequestParam("message") String message,
                              @RequestParam("success") boolean success,
                              Model model) {
    model.addAttribute("message", message);
    model.addAttribute("success", success);
    return "reset-result";
}
    
    // ... Keep your forgot-password methods here as they were ...
}
