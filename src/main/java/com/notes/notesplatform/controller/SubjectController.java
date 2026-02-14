package com.notes.notesplatform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.notes.notesplatform.model.Subject;
import com.notes.notesplatform.model.ClassEntity;
import com.notes.notesplatform.model.Course;
import com.notes.notesplatform.repository.SubjectRepository;
import com.notes.notesplatform.repository.ClassRepository;
import com.notes.notesplatform.repository.CourseRepository;

import java.util.List;

@RestController
@RequestMapping("/subjects")
public class SubjectController {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    @PostMapping
    public Subject createSubject(
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long courseId,
            @RequestBody Subject subject) {

        if (classId != null) {
            ClassEntity classEntity = classRepository.findById(classId)
                    .orElseThrow(() -> new RuntimeException("Class not found with id: " + classId));
            subject.setClassEntity(classEntity);
        } else if (courseId != null) {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
            subject.setCourse(course);
        } else {
            throw new RuntimeException("Either classId or courseId must be provided.");
        }

        return subjectRepository.save(subject);
    }
}