package com.notes.notesplatform.controller;

import com.notes.notesplatform.model.Chapter;
import com.notes.notesplatform.repository.ChapterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ChapterPageController {

    @Autowired
    private ChapterRepository chapterRepository;

    @GetMapping("/chapter-page")
    public String showChapters(@RequestParam(required = false) Long subjectId,
                               Model model) {
        List<Chapter> chapters;

        if (subjectId != null) {
            chapters = chapterRepository.findBySubjectIdAndDeletedFalse(subjectId);
        } else {
            chapters = List.of();
        }

        model.addAttribute("chapters", chapters);
        return "chapters";
    }
}