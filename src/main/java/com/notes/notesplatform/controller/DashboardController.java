/*package com.notes.notesplatform.controller;

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

    
   

@GetMapping("/dashboard")
public String redirectDashboard(Authentication authentication) {
    if (authentication != null) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_ADMIN") 
                            || a.getAuthority().equalsIgnoreCase("ADMIN"));
        
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
}*/

package com.notes.notesplatform.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.notes.notesplatform.model.User;
import com.notes.notesplatform.model.PurchasedNote;
import com.notes.notesplatform.repository.UserRepository;
import com.notes.notesplatform.repository.PurchasedNoteRepository;
import com.notes.notesplatform.service.StorageService;

@Controller
public class DashboardController {

    private final UserRepository userRepository;
    private final PurchasedNoteRepository purchasedNoteRepository;
    private final StorageService storageService;

    public DashboardController(UserRepository userRepository,
                               PurchasedNoteRepository purchasedNoteRepository,
                               StorageService storageService) {

        this.userRepository = userRepository;
        this.purchasedNoteRepository = purchasedNoteRepository;
        this.storageService = storageService;
    }

    // ==============================
    // ROLE BASED DASHBOARD REDIRECT
    // ==============================
    @GetMapping("/dashboard")
    public String redirectDashboard(Authentication authentication) {

        if (authentication != null) {

            boolean isAdmin = authentication.getAuthorities()
                    .stream()
                    .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_ADMIN")
                            || a.getAuthority().equalsIgnoreCase("ADMIN"));

            return isAdmin
                    ? "redirect:/admin/dashboard"
                    : "redirect:/user/dashboard";
        }

        return "redirect:/login";
    }

    // ==============================
    // ADMIN DASHBOARD
    // ==============================
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
      
        List <User> User= userRepository.findAll();
        model.addAttribute("User", User);

        return "admindashboard";
    }

    // ==============================
    // USER DASHBOARD
    // ==============================
    @GetMapping("/user/dashboard")
    public String userDashboard(Model model, Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userRepository
                .findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<PurchasedNote> purchasedNotes =
                purchasedNoteRepository.findByUserWithNotes(user);

        // 🔐 Map for Signed URLs
        Map<Long, String> dashboardSecureUrls = new HashMap<>();

        for (PurchasedNote purchase : purchasedNotes) {

            String signedUrl =
                    storageService.getSignedUrl(
                            purchase.getNote().getFileUrl()
                    );

            dashboardSecureUrls.put(
                    purchase.getNote().getId(),
                    signedUrl
            );
        }

        model.addAttribute("user", user);
        model.addAttribute("purchasedNotes", purchasedNotes);
        model.addAttribute("dashboardSecureUrls", dashboardSecureUrls);

        return "userdashboard";
    }
}