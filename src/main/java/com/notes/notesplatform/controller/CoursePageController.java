package com.notes.notesplatform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.notes.notesplatform.repository.ClassRepository;
import com.notes.notesplatform.repository.CourseRepository;
import java.util.List;
import java.util.Map;

import com.notes.notesplatform.model.Course;

@Controller
public class CoursePageController {

    @Autowired
    private CourseRepository CourseRepository;

    @Autowired
    private ClassRepository classRepository;

    @GetMapping("/courses-page")
    public String showCourses(Model model) {
        List<Course> courses = CourseRepository.findByDeletedFalse();
        model.addAttribute("courses", courses);
        return "courses"; // Looks for courses.html in templates folder
    }

    @GetMapping("/courses/{courseId}/has-classes")
@ResponseBody
public Map<String, Boolean> hasClasses(@PathVariable Long courseId) {
    boolean hasClasses = !classRepository.findByCourseIdAndDeletedFalse(courseId).isEmpty();
    return Map.of("hasClasses", hasClasses);
}
}