package com.notes.notesplatform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.notes.notesplatform.model.Note;
import com.notes.notesplatform.model.Chapter;
import com.notes.notesplatform.repository.NoteRepository;
import com.notes.notesplatform.repository.ChapterRepository;

import java.util.List;

@RestController
@RequestMapping("/notes")
public class NoteController {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private ChapterRepository chapterRepository;


    @PostMapping
    public Note createNote(@RequestParam Long chapterId, @RequestBody Note note) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Chapter not found with id: " + chapterId));
        note.setChapter(chapter);
        return noteRepository.save(note);
    }
}