package com.notes.notesplatform.controller;

import com.notes.notesplatform.model.*;
import com.notes.notesplatform.repository.*;
import com.notes.notesplatform.service.*;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.notes.notesplatform.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StorageService storageService;

    //@GetMapping("/users")
    //public  ShowUserList(){
      //  List<User> allUsers= userRepository.findAll();
       // return allUsers;
    //}

    @GetMapping("/users")
    public List<User> ShowUserList(){
        return userRepository.findAll();
    }

    // New course page
    @GetMapping("/new-course")
    public String showNewCourseForm(Model model) {
        model.addAttribute("course", new Course());
        return "new-course";
    }

    @PostMapping("/new-course")
    public String createCourse(@ModelAttribute Course course,
            @RequestParam(value = "noteFiles", required = false) List<MultipartFile> noteFiles) {

        final int[] fileIndex = { 0 };

        if (course.getSubjects() != null) {
            List<Subject> subjects = course.getSubjects();
            for (int i = 0; i < subjects.size(); i++) {
                Subject subject = subjects.get(i);
                subject.setCourse(course);
                subject.setSubjectOrder(i);
                processChapters(subject, subject.getChapters(), noteFiles, fileIndex);
            }
        }

        if (course.getClasses() != null) {
            List<ClassEntity> classes = course.getClasses();
            for (int i = 0; i < classes.size(); i++) {
                ClassEntity classEntity = classes.get(i);
                classEntity.setCourse(course);
                classEntity.setClassOrder(i);

                if (classEntity.getSubjects() != null) {
                    List<Subject> classSubjects = classEntity.getSubjects();
                    for (int j = 0; j < classSubjects.size(); j++) {
                        Subject subject = classSubjects.get(j);
                        subject.setClassEntity(classEntity);
                        subject.setCourse(course);
                        subject.setSubjectOrder(j);
                        processChapters(subject, subject.getChapters(), noteFiles, fileIndex);
                    }
                }
            }
        }

        courseRepository.save(course);
        return "redirect:/admin/manage-courses";
    }

    private void processChapters(Subject subject, List<Chapter> chapters, List<MultipartFile> files, int[] index) {
        if (chapters != null) {
            for (Chapter chapter : chapters) {

                chapter.setSubject(subject);

                if (chapter.getNotes() != null) {
                    for (Note note : chapter.getNotes()) {
                        note.setChapter(chapter);

                        if (files != null && index[0] < files.size() && !files.get(index[0]).isEmpty()) {
                            try {
                                String filePath = storageService.uploadFile(files.get(index[0]++));
                                note.setFileUrl(filePath);
                            } catch (Exception e) {

                                if (e.getMessage().contains("base 16")) {
                                    System.out
                                            .println("Warning: Checksum validation failed, but file likely uploaded.");
                                } else {
                                    throw new RuntimeException("Supabase upload failed", e);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @GetMapping("/manage-courses")
    public String manageCourses(Model model) {
        List<Course> courses = courseRepository.findAll();
        model.addAttribute("courses", courses);
        return "new-course";
    }

    @PostMapping("/add-class")
    public String addClass(@RequestParam Long courseId, @RequestParam String className) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        ClassEntity classEntity = new ClassEntity(className, course);
        classRepository.save(classEntity);
        return "redirect:/admin/manage-courses";
    }

    @PostMapping("/add-subject")
    public String addSubject(@RequestParam Long classId,
            @RequestParam String subjectName) {

        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        Course course = classEntity.getCourse();

        Subject subject = new Subject();
        subject.setName(subjectName);
        subject.setClassEntity(classEntity);
        subject.setCourse(course);

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

    @PostMapping("/add-note")
    public String addNote(@RequestParam Long chapterId,
            @RequestParam String title,
            @RequestParam("file") MultipartFile file,
            @RequestParam BigDecimal price,
            @RequestParam(defaultValue = "false") boolean isFree) {

        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));

        try {
            String filePath = storageService.uploadFile(file);
            Note note = new Note(title, filePath, price, isFree, chapter);
            noteRepository.save(note);
        } catch (Exception e) {
            throw new RuntimeException("Single upload failed", e);
        }
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
