package com.notes.notesplatform.controller;

import com.notes.notesplatform.model.Subject;
import com.notes.notesplatform.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SubjectPageController {

    @Autowired
    private SubjectRepository subjectRepository;

    @GetMapping("/subject-page")
    public String showSubjects(@RequestParam(required = false) Long courseId,
                               @RequestParam(required = false) Long classId,
                               Model model) {
        List<Subject> subjects;

        if (classId != null) {
            subjects = subjectRepository.findByClassEntityIdAndDeletedFalse(classId);
        } else if (courseId != null) {
            subjects = subjectRepository.findByCourse_IdAndDeletedFalse(courseId);
        } else {
            subjects = List.of();
        }

        model.addAttribute("subjects", subjects);
        return "subjects";
    }
}