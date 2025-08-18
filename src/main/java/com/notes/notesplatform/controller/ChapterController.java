package com.notes.notesplatform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.notes.notesplatform.model.Chapter;
import com.notes.notesplatform.model.Subject;
import com.notes.notesplatform.repository.ChapterRepository;
import com.notes.notesplatform.repository.SubjectRepository;

import java.util.List;

@RestController
@RequestMapping("/chapters")
public class ChapterController {

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @GetMapping
    public List<Chapter> getAllChapters() {
        return chapterRepository.findAll();
    }

    @PostMapping
    public Chapter createChapter(@RequestParam Long subjectId, @RequestBody Chapter chapter) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));
        chapter.setSubject(subject);
        return chapterRepository.save(chapter);
    }
}