package com.notes.notesplatform.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// Import the necessary repositories and models
import com.notes.notesplatform.model.User;
import com.notes.notesplatform.model.PurchasedNote;
import com.notes.notesplatform.repository.UserRepository;
import com.notes.notesplatform.repository.PurchasedNoteRepository;

@Controller
public class DashboardController {

    private final UserRepository userRepository;
    private final PurchasedNoteRepository purchasedNoteRepository;

    // Use constructor injection to get the repositories
    public DashboardController(UserRepository userRepository, PurchasedNoteRepository purchasedNoteRepository) {
        this.userRepository = userRepository;
        this.purchasedNoteRepository = purchasedNoteRepository;
    }

    // Main entry point for all dashboards. Redirects based on user role.
    @GetMapping("/dashboard")
    public String redirectDashboard(Authentication authentication) {
        if (authentication != null) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            return isAdmin ? "redirect:/admin/dashboard" : "redirect:/user/dashboard";
        }
        return "redirect:/login";
    }

    // Admin dashboard controller. This is where you would add admin-specific logic.
    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        // Add admin-specific logic here, e.g., fetching user lists, site statistics, etc.
        // For now, it just returns the view name.
        return "admindashboard"; // Template: src/main/resources/templates/admindashboard.html
    } 

    // User dashboard controller. This is where we add the logic to display purchased notes.
    @GetMapping("/user/dashboard")
    public String userDashboard(Model model, Principal principal) {
        // Ensure a user is authenticated
        if (principal == null) {
            return "redirect:/login";
        }

        // 1. Get the authenticated user's details
        User user = userRepository.findByEmail(principal.getName())
                                  .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // 2. Fetch the list of all notes purchased by this user
        List<PurchasedNote> purchasedNotes = purchasedNoteRepository.findByUserWithNotes(user);

        // 3. Add the user and purchased notes to the Model
        model.addAttribute("user", user);
        model.addAttribute("purchasedNotes", purchasedNotes);

        // 4. Return the Thymeleaf template for the user dashboard
        return "userdashboard"; // Template: src/main/resources/templates/userdashboard.html
    }
}