package com.notes.notesplatform.controller;

import com.notes.notesplatform.model.ClassEntity;
import com.notes.notesplatform.repository.ClassRepository;
import com.notes.notesplatform.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ClassPageController {

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping("/courses/{courseId}/classes")
    public String getClassesByCourse(@PathVariable Long courseId, Model model) {
        List<ClassEntity> classes = classRepository.findByCourseIdAndDeletedFalse(courseId);
        model.addAttribute("classes", classes);
        model.addAttribute("course", courseRepository.findById(courseId).orElse(null));
        return "classes";
    }
}