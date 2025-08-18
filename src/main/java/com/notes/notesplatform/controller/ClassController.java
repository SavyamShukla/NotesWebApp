package com.notes.notesplatform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.notes.notesplatform.model.ClassEntity;
import com.notes.notesplatform.model.Course;
import com.notes.notesplatform.repository.ClassRepository;
import com.notes.notesplatform.repository.CourseRepository;

import java.util.List;

@RestController
@RequestMapping("/classes")
public class ClassController {

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping
    public List<ClassEntity> getAllClasses() {
        return classRepository.findAll();
    }
    @GetMapping("/by-course/{courseId}")
public List<ClassEntity> getClassesByCourse(@PathVariable Long courseId) {
    return classRepository.findByCourseIdAndDeletedFalse(courseId);
}

    @PostMapping
    public ClassEntity createClass(@RequestParam Long courseId, @RequestBody ClassEntity classEntity) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
        classEntity.setCourse(course);
        return classRepository.save(classEntity);
    }
}

