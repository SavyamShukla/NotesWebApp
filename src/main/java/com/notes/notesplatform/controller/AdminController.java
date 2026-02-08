package com.notes.notesplatform.controller;

import com.notes.notesplatform.model.*;
import com.notes.notesplatform.repository.*;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

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

   
    // New course page
    @GetMapping("/new-course")
    public String showNewCourseForm(Model model) {
        model.addAttribute("course", new Course());
        return "new-course";
    }




@PostMapping("/new-course")
public String createCourse(@ModelAttribute Course course) {

    // Handle direct subjects
    

    if (course.getSubjects() != null) {
    for (Subject subject : course.getSubjects()) {
        subject.setCourse(course);

        if (subject.getChapters() != null) {
            for (Chapter chapter : subject.getChapters()) {
                chapter.setSubject(subject);

                if (chapter.getNotes() != null) {
                    for (Note note : chapter.getNotes()) {
                        note.setChapter(chapter);
                    }
                }
            }
        }
    }
}

    
    if (course.getClasses() != null) {
        for (ClassEntity classEntity : course.getClasses()) {
            classEntity.setCourse(course);

            if (classEntity.getSubjects() != null) {
                for (Subject subject : classEntity.getSubjects()) {
                    subject.setClassEntity(classEntity);
                    subject.setCourse(course); 

                    if (subject.getChapters() != null) {
                        for (Chapter chapter : subject.getChapters()) {
                            chapter.setSubject(subject);

                            if (chapter.getNotes() != null) {
                                for (Note note : chapter.getNotes()) {
                                    note.setChapter(chapter);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    courseRepository.save(course);
    return "redirect:/admin/dashboard";
}



    
    // Manage existing courses
    @GetMapping("/manage-courses")
    public String manageCourses(Model model) {
        List<Course> courses = courseRepository.findAll();
        model.addAttribute("courses", courses);
        return "manage-courses";
    }

    // Add Class under a Course
    @PostMapping("/add-class")
    public String addClass(@RequestParam Long courseId, @RequestParam String className) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        ClassEntity classEntity = new ClassEntity(className, course);
        classRepository.save(classEntity);
        return "redirect:/admin/manage-courses";
    }

    // Add Subject under a Class
    /*@PostMapping("/add-subject")
    public String addSubject(@RequestParam Long classId, @RequestParam String subjectName) {
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));
        Subject subject = new Subject(subjectName, classEntity);
        subjectRepository.save(subject);
        return "redirect:/admin/manage-courses";
    }*/


        @PostMapping("/add-subject")
public String addSubject(@RequestParam Long classId,
                         @RequestParam String subjectName) {

    ClassEntity classEntity = classRepository.findById(classId)
            .orElseThrow(() -> new RuntimeException("Class not found"));

    Course course = classEntity.getCourse();   // 🔥 get parent course

    Subject subject = new Subject();
    subject.setName(subjectName);
    subject.setClassEntity(classEntity);
    subject.setCourse(course);                 // 🔥 VERY IMPORTANT

    subjectRepository.save(subject);

    return "redirect:/admin/manage-courses";
}


    // Add Chapter under a Subject
    @PostMapping("/add-chapter")
    public String addChapter(@RequestParam Long subjectId, @RequestParam String chapterName) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        Chapter chapter = new Chapter(chapterName, subject);
        chapterRepository.save(chapter);
        return "redirect:/admin/manage-courses";
    }

    // Add Note under a Chapter
    @PostMapping("/add-note")
    public String addNote(@RequestParam Long chapterId,
                          @RequestParam String title,
                          @RequestParam String fileUrl,
                          @RequestParam BigDecimal price,
                          @RequestParam(defaultValue = "false") boolean isFree) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));
        Note note = new Note(title, fileUrl, price, isFree, chapter);
        noteRepository.save(note);
        return "redirect:/admin/manage-courses";
    }

    // Delete any entity
    @PostMapping("/delete-course/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseRepository.deleteById(id);
        return "redirect:/admin/manage-courses";
    }

    @PostMapping("/delete-class/{id}")
    public String deleteClass(@PathVariable Long id) {
        classRepository.deleteById(id);
        return "redirect:/admin/manage-courses";
    }

    @PostMapping("/delete-subject/{id}")
    public String deleteSubject(@PathVariable Long id) {
        subjectRepository.deleteById(id);
        return "redirect:/admin/manage-courses";
    }

    @PostMapping("/delete-chapter/{id}")
    public String deleteChapter(@PathVariable Long id) {
        chapterRepository.deleteById(id);
        return "redirect:/admin/manage-courses";
    }

    @PostMapping("/delete-note/{id}")
    public String deleteNote(@PathVariable Long id) {
        noteRepository.deleteById(id);
        return "redirect:/admin/manage-courses";
    }




   

   
    
}