package com.notes.notesplatform.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import com.notes.notesplatform.model.User;
import com.notes.notesplatform.model.PurchasedNote;
import com.notes.notesplatform.repository.UserRepository;
import com.notes.notesplatform.repository.PurchasedNoteRepository;

@Controller
public class DashboardController {

    private final UserRepository userRepository;
    private final PurchasedNoteRepository purchasedNoteRepository;

    
    public DashboardController(UserRepository userRepository, PurchasedNoteRepository purchasedNoteRepository) {
        this.userRepository = userRepository;
        this.purchasedNoteRepository = purchasedNoteRepository;
    }

    
    /*etMapping("/dashboard")
    public String redirectDashboard(Authentication authentication) {
        if (authentication != null) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            return isAdmin ? "redirect:/admin/dashboard" : "redirect:/user/dashboard";
        }
        return "redirect:/login";
    }
       */
    @GetMapping("/dashboard")
public String redirectDashboard(Authentication authentication) {
    if (authentication != null) {
        // Check for "ROLE_ADMIN" OR just "ADMIN" to be safe
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));
        
        System.out.println("User authorities: " + authentication.getAuthorities()); // Debugging line
        
        return isAdmin ? "redirect:/admin/dashboard" : "redirect:/user/dashboard";
    }
    return "redirect:/login";
}

    
    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        
        return "admindashboard"; 
    } 

    
    @GetMapping("/user/dashboard")
    public String userDashboard(Model model, Principal principal) {
        
        if (principal == null) {
            return "redirect:/login";
        }

        
        User user = userRepository.findByEmail(principal.getName())
                                  .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        
        List<PurchasedNote> purchasedNotes = purchasedNoteRepository.findByUserWithNotes(user);

        
        model.addAttribute("user", user);
        model.addAttribute("purchasedNotes", purchasedNotes);

        
        return "userdashboard"; 
    }
}