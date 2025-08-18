package com.notes.notesplatform.controller;

import com.notes.notesplatform.model.*;
import com.notes.notesplatform.repository.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/existing-courses")
public class ExistingCourseController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private NoteRepository noteRepository;

    @GetMapping
    public String existingCoursesPage() {
        return "existing-course";
    }

    @GetMapping("/all")
    @ResponseBody
    public List<Course> getAllCourses() {
        return courseRepository.findByDeletedFalse();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        Optional<Course> course = courseRepository.findById(id);
        return course.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @Valid @RequestBody Course updatedCourse) {
        return courseRepository.findById(id)
                .map(existing -> {
                    existing.setName(updatedCourse.getName());
                    existing.setDescription(updatedCourse.getDescription());
                    Course saved = courseRepository.save(existing);
                    return ResponseEntity.ok(saved);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ---- Soft delete endpoints ----

    @PostMapping("/soft-delete-course/{id}")
    @ResponseBody
    public ResponseEntity<?> softDeleteCourse(@PathVariable Long id) {
        return courseRepository.findById(id)
                .map(course -> {
                    course.setDeleted(true);
                    courseRepository.save(course);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/soft-delete-class/{id}")
    @ResponseBody
    public ResponseEntity<?> softDeleteClass(@PathVariable Long id) {
        return classRepository.findById(id)
                .map(cls -> {
                    cls.setDeleted(true);
                    classRepository.save(cls);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/soft-delete-subject/{id}")
    @ResponseBody
    public ResponseEntity<?> softDeleteSubject(@PathVariable Long id) {
        return subjectRepository.findById(id)
                .map(sub -> {
                    sub.setDeleted(true);
                    subjectRepository.save(sub);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/soft-delete-chapter/{id}")
    @ResponseBody
    public ResponseEntity<?> softDeleteChapter(@PathVariable Long id) {
        return chapterRepository.findById(id)
                .map(ch -> {
                    ch.setDeleted(true);
                    chapterRepository.save(ch);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/soft-delete-note/{id}")
    @ResponseBody
    public ResponseEntity<?> softDeleteNote(@PathVariable Long id) {
        return noteRepository.findById(id)
                .map(note -> {
                    note.setDeleted(true);
                    noteRepository.save(note);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ---- New endpoints for hierarchy ----

    @GetMapping("/{courseId}/classes")
    @ResponseBody
    public List<ClassEntity> getClassesByCourse(@PathVariable Long courseId) {
        return classRepository.findByCourseIdAndDeletedFalse(courseId);
    }

    @GetMapping("/classes/{classId}/subjects")
    @ResponseBody
    public List<Subject> getSubjectsByClass(@PathVariable Long classId) {
        return subjectRepository.findByClassEntityIdAndDeletedFalse(classId);
    }

    @GetMapping("/subjects/{subjectId}/chapters")
    @ResponseBody
    public List<Chapter> getChaptersBySubject(@PathVariable Long subjectId) {
        return chapterRepository.findBySubjectIdAndDeletedFalse(subjectId);
    }

    @GetMapping("/chapters/{chapterId}/notes")
    @ResponseBody
    public List<Note> getNotesByChapter(@PathVariable Long chapterId) {
        return noteRepository.findByChapterIdAndDeletedFalse(chapterId);
    }

    @PutMapping("/update-note/{id}")
    @ResponseBody
    public ResponseEntity<Note> updateNote(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        return noteRepository.findById(id)
                .map(note -> {
                    if (payload.containsKey("fileUrl")) {
                        note.setFileUrl((String) payload.get("fileUrl"));
                    }
                    if (payload.containsKey("price")) {
                        note.setPrice(Double.parseDouble(payload.get("price").toString()));
                    }
                    if (payload.containsKey("isFree")) {
                        note.setFree((Boolean) payload.get("isFree"));
                    }
                    Note saved = noteRepository.save(note);
                    return ResponseEntity.ok(saved);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}