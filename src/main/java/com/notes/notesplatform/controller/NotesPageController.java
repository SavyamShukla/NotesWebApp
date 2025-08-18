package com.notes.notesplatform.controller;

import com.notes.notesplatform.model.Note;
import com.notes.notesplatform.model.PurchasedNote;
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
    return "notes";
}


   @PostMapping("/buyNote")
public String buyNote(@RequestParam Long noteId, Principal principal) {
    User user = userRepository.findByEmail(principal.getName()).orElseThrow();
    Note note = noteRepository.findById(noteId).orElseThrow();

    if (!purchasedNoteRepository.existsByUserAndNote(user, note)) {
        PurchasedNote purchase = new PurchasedNote();
        purchase.setUser(user);
        purchase.setNote(note);
        purchase.setPurchasedAt(LocalDateTime.now());

        purchasedNoteRepository.save(purchase);
    }

    return "redirect:/notes?chapterId=" + note.getChapter().getId(); // redirect back
}



}