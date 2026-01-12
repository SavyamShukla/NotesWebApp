/*package com.notes.notesplatform.controller;

import com.notes.notesplatform.model.Note;
import com.notes.notesplatform.model.PurchasedNote;
import org.springframework.beans.factory.annotation.Value;
import com.notes.notesplatform.model.User;
import com.notes.notesplatform.repository.NoteRepository;
import com.notes.notesplatform.repository.PurchasedNoteRepository;
import com.notes.notesplatform.repository.UserRepository;
import com.notes.notesplatform.service.S3Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


import com.notes.notesplatform.service.S3Service; // Your S3 Service
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication; // Use Authentication for more details
import org.springframework.web.bind.annotation.ResponseBody;
import java.net.URL;


import org.slf4j.Logger; // Use SLF4J for logging
import org.slf4j.LoggerFactory;



@Controller
public class NotesPageController {

    private static final Logger logger = LoggerFactory.getLogger(NotesPageController.class);

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PurchasedNoteRepository purchasedNoteRepository;
   

    @GetMapping("/notes")
public String showNotes(@RequestParam(required = false) Long chapterId,
                        Model model,
                        Principal principal) {

    List<Note> notes = new ArrayList<>();
    Map<Long, Boolean> userHasNote = new HashMap<>();

    if (chapterId != null) {
        notes = noteRepository.findByChapterIdAndDeletedFalse(chapterId);

        if (principal != null) {
            
            User user = userRepository.findByEmail(principal.getName()).orElse(null);

            for (Note note : notes) {
                boolean purchased = purchasedNoteRepository.existsByUserAndNote(user, note);
                userHasNote.put(note.getId(), purchased);
            }
        }
    }

    model.addAttribute("notes", notes);
    model.addAttribute("userHasNote", userHasNote);
    model.addAttribute("razorpayKey", razorpayKeyId);
    return "notes";
}
    @GetMapping("/get-pdf-url")
    @ResponseBody // Ensures the response is JSON
    public ResponseEntity<Map<String, String>> getPdfPresignedUrl(@RequestParam String key, Authentication authentication) {

        // 1. Find the Note by its S3 key
        Optional<Note> noteOptional = noteRepository.findByS3KeyAndDeletedFalse(key); // Assumes you add findByS3KeyAndDeletedFalse
        if (noteOptional.isEmpty()) {
            logger.warn("Attempted to access non-existent or deleted note with S3 key: {}", key);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Note not found."));
        }
        Note note = noteOptional.get();

        // 2. Check if the note is free
        boolean isFreeNote = note.isFree(); // Assumes Note object has isFree() method

        // 3. Authorization Checks if the note is NOT free
        if (!isFreeNote) {
            // 3a. Check if user is authenticated
            if (authentication == null || !authentication.isAuthenticated()) {
                logger.warn("Unauthenticated attempt to access paid note with S3 key: {}", key);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Please log in to access this note."));
            }

            // 3b. Check if the authenticated user has purchased this specific note
            String userEmail = authentication.getName(); // Assuming email is username
            Optional<User> userOptional = userRepository.findByEmail(userEmail);
            if (userOptional.isEmpty()) {
                 logger.error("Authenticated user {} not found in database during PDF access check.", userEmail);
                 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "User account error."));
            }
            User user = userOptional.get();

            boolean hasPurchased = purchasedNoteRepository.existsByUserAndNote(user, note);
            if (!hasPurchased) {
                logger.warn("User {} attempted to access paid note (key: {}) without purchase.", userEmail, key);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "You have not purchased this note."));
            }
            logger.info("User {} authorized to access purchased note (key: {}).", userEmail, key);
        } else {
             logger.info("Access granted for free note (key: {}).", key);
        }

        // 4. If all checks pass, generate the Presigned URL
        URL url = s3Service.getPresignedUrl(key);

        // 5. Return the URL or an error
        if (url != null) {
            return ResponseEntity.ok(Map.of("url", url.toString()));
        } else {
            // S3Service logs the specific error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Could not generate file URL. Please contact support if this persists."));
        }
    }

   

}
    
   */




















package com.notes.notesplatform.controller;

import com.notes.notesplatform.model.Note;
import com.notes.notesplatform.model.PurchasedNote;
import org.springframework.beans.factory.annotation.Value;
import com.notes.notesplatform.model.User;
import com.notes.notesplatform.repository.NoteRepository;
import com.notes.notesplatform.repository.PurchasedNoteRepository;
import com.notes.notesplatform.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class NotesPageController {

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;



    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PurchasedNoteRepository purchasedNoteRepository;
   

    @GetMapping("/notes")
public String showNotes(@RequestParam(required = false) Long chapterId,
                        Model model,
                        Principal principal) {

    List<Note> notes = new ArrayList<>();
    Map<Long, Boolean> userHasNote = new HashMap<>();

    if (chapterId != null) {
        notes = noteRepository.findByChapterIdAndDeletedFalse(chapterId);

        if (principal != null) {
            
            User user = userRepository.findByEmail(principal.getName()).orElse(null);

            for (Note note : notes) {
                boolean purchased = purchasedNoteRepository.existsByUserAndNote(user, note);
                userHasNote.put(note.getId(), purchased);
            }
        }
    }

    model.addAttribute("notes", notes);
    model.addAttribute("userHasNote", userHasNote);
    model.addAttribute("razorpayKey", razorpayKeyId);
    return "notes";
}

   

}