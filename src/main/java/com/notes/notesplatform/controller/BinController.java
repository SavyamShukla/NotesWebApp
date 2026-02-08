package com.notes.notesplatform.controller;

import com.notes.notesplatform.model.*;
import com.notes.notesplatform.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/bin")
public class BinController {

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

    
    
    @GetMapping("/courses")
    public List<Map<String, Object>> getDeletedCourses() {
        List<Course> deleted = courseRepository.findByDeletedTrue();
        return deleted.stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("name", c.getName());
            map.put("fullPath", c.getName());
            return map;
        }).toList();
    }

    @GetMapping("/classes")
    public List<Map<String, Object>> getDeletedClasses() {
        List<ClassEntity> deleted = classRepository.findByDeletedTrue();
        return deleted.stream().map(cls -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", cls.getId());
            map.put("name", cls.getName());
            String courseName = cls.getCourse() != null ? cls.getCourse().getName() : "Unknown Course";
            map.put("fullPath", courseName + " > " + cls.getName());
            return map;
        }).toList();
    }

    @GetMapping("/subjects")
    public List<Map<String, Object>> getDeletedSubjects() {
        List<Subject> deleted = subjectRepository.findByDeletedTrue();
        return deleted.stream().map(sub -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", sub.getId());
            map.put("name", sub.getName());
            String courseName = sub.getClassEntity() != null && sub.getClassEntity().getCourse() != null
                    ? sub.getClassEntity().getCourse().getName() : "Unknown Course";
            String className = sub.getClassEntity() != null ? sub.getClassEntity().getName() : "Unknown Class";
            map.put("fullPath", courseName + " > " + className + " > " + sub.getName());
            return map;
        }).toList();
    }

    @GetMapping("/chapters")
    public List<Map<String, Object>> getDeletedChapters() {
        List<Chapter> deleted = chapterRepository.findByDeletedTrue();
        return deleted.stream().map(ch -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", ch.getId());
            map.put("name", ch.getName());
            String courseName = "Unknown Course";
            String className = "Unknown Class";
            String subjectName = "Unknown Subject";
            if (ch.getSubject() != null) {
                subjectName = ch.getSubject().getName();
                if (ch.getSubject().getClassEntity() != null) {
                    className = ch.getSubject().getClassEntity().getName();
                    if (ch.getSubject().getClassEntity().getCourse() != null) {
                        courseName = ch.getSubject().getClassEntity().getCourse().getName();
                    }
                }
            }
            map.put("fullPath", courseName + " > " + className + " > " + subjectName + " > " + ch.getName());
            return map;
        }).toList();
    }

   
    @GetMapping("/notes")
public List<Map<String, Object>> getDeletedNotes() {
    return noteRepository.findDeletedNotesWithFullPath();
}

    
    @PutMapping("/restore/{type}/{id}")
    public ResponseEntity<String> restoreEntity(@PathVariable String type, @PathVariable Long id) {
        switch (type.toLowerCase()) {
            case "courses" -> {
                Optional<Course> course = courseRepository.findById(id);
                if (course.isPresent()) {
                    course.get().setDeleted(false);
                    courseRepository.save(course.get());
                    return ResponseEntity.ok("Course restored");
                }
            }
            case "classes" -> {
                Optional<ClassEntity> cls = classRepository.findById(id);
                if (cls.isPresent()) {
                    cls.get().setDeleted(false);
                    classRepository.save(cls.get());
                    return ResponseEntity.ok("Class restored");
                }
            }
            case "subjects" -> {
                Optional<Subject> sub = subjectRepository.findById(id);
                if (sub.isPresent()) {
                    sub.get().setDeleted(false);
                    subjectRepository.save(sub.get());
                    return ResponseEntity.ok("Subject restored");
                }
            }
            case "chapters" -> {
                Optional<Chapter> ch = chapterRepository.findById(id);
                if (ch.isPresent()) {
                    ch.get().setDeleted(false);
                    chapterRepository.save(ch.get());
                    return ResponseEntity.ok("Chapter restored");
                }
            }
            case "notes" -> {
                Optional<Note> note = noteRepository.findById(id);
                if (note.isPresent()) {
                    note.get().setDeleted(false);
                    noteRepository.save(note.get());
                    return ResponseEntity.ok("Note restored");
                }
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/permanent/{type}/{id}")
public ResponseEntity<?> permanentlyDelete(
        @PathVariable String type,
        @PathVariable Long id) {

    switch (type.toLowerCase()) {

        case "courses" -> courseRepository.deleteById(id);
        case "classes" -> classRepository.deleteById(id);
        case "subjects" -> subjectRepository.deleteById(id);
        case "chapters" -> chapterRepository.deleteById(id);
        case "notes" -> noteRepository.deleteById(id);
        default -> {
            return ResponseEntity.badRequest().body("Invalid type");
        }
    }

    return ResponseEntity.ok().build();
}

}