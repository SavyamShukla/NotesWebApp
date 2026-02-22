package com.notes.notesplatform.controller;

import com.notes.notesplatform.model.Note;
import com.notes.notesplatform.model.PurchasedNote;
import org.springframework.beans.factory.annotation.Value;
import com.notes.notesplatform.model.User;
import com.notes.notesplatform.repository.NoteRepository;
import com.notes.notesplatform.repository.PurchasedNoteRepository;
import com.notes.notesplatform.repository.UserRepository;
import com.notes.notesplatform.service.StorageService;

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
/*
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

}*/

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

    @Autowired
    private StorageService storageService; // 1. Inject your StorageService

    @GetMapping("/notes")
    public String showNotes(@RequestParam(required = false) Long chapterId,
                            Model model,
                            Principal principal) {

        List<Note> notes = new ArrayList<>();
        Map<Long, Boolean> userHasNote = new HashMap<>();
        Map<Long, String> secureUrls = new HashMap<>(); // 2. Map to hold temporary links

        if (chapterId != null) {
            notes = noteRepository.findByChapterIdAndDeletedFalse(chapterId);

            if (principal != null) {
                User user = userRepository.findByEmail(principal.getName()).orElse(null);

                for (Note note : notes) {
                    //boolean purchased = purchasedNoteRepository.existsByUserAndNote(user, note);
                    //userHasNote.put(note.getId(), purchased);
                    boolean purchased =
purchasedNoteRepository
.existsByUserIdAndNoteId(user.getId(), note.getId());
userHasNote.put(note.getId(), purchased);
System.out.println("USER ID: " + user.getId()
    + " NOTE ID: " + note.getId()
    + " PURCHASED: " + purchased);

                    // 3. Generate Signed URL only if the user has access
                    if (note.isFree() || purchased) {
                        String signedUrl = storageService.getSignedUrl(note.getFileUrl());
                        secureUrls.put(note.getId(), signedUrl);
                    }
                }
            } else {
                // For guests (not logged in), only generate links for FREE notes
                for (Note note : notes) {
                    if (note.isFree()) {
                        String signedUrl = storageService.getSignedUrl(note.getFileUrl());
                        secureUrls.put(note.getId(), signedUrl);
                    }
                }
            }
        }

        model.addAttribute("notes", notes);
        model.addAttribute("userHasNote", userHasNote);
        model.addAttribute("secureUrls", secureUrls);
        System.out.println(secureUrls); // 4. Pass URLs to the UI
        model.addAttribute("razorpayKey", razorpayKeyId);
        return "notes";
    }
}