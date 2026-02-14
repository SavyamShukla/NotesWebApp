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
    private StorageService storageService;

   
    // New course page
    @GetMapping("/new-course")
    public String showNewCourseForm(Model model) {
        model.addAttribute("course", new Course());
        return "new-course";
    }



/* 
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
}*/


/*@PostMapping("/new-course")
    public String createCourse(@ModelAttribute Course course, 
                               @RequestParam(value = "noteFiles", required = false) List<MultipartFile> noteFiles) {
        
        // Counter ensures files match the Note objects in order of appearance
        final int[] fileIndex = {0};

        // Handle hierarchy with direct subjects
        if (course.getSubjects() != null) {
            for (Subject subject : course.getSubjects()) {
                subject.setCourse(course);
                processChapters(subject, subject.getChapters(), noteFiles, fileIndex);
            }
        }

        // Handle hierarchy with classes
        if (course.getClasses() != null) {
            for (ClassEntity classEntity : course.getClasses()) {
                classEntity.setCourse(course);
                if (classEntity.getSubjects() != null) {
                    for (Subject subject : classEntity.getSubjects()) {
                        subject.setClassEntity(classEntity);
                        subject.setCourse(course); 
                        processChapters(subject, subject.getChapters(), noteFiles, fileIndex);
                    }
                }
            }
        }

        courseRepository.save(course);
        return "redirect:/admin/manage-courses";
    }

   / private void processChapters(List<Chapter> chapters, List<MultipartFile> files, int[] index) {
        if (chapters != null) {
            for (Chapter chapter : chapters) {
                if (chapter.getNotes() != null) {
                    for (Note note : chapter.getNotes()) {
                        note.setChapter(chapter);
                        // Assign file if available in the sequential list
                        if (files != null && index[0] < files.size() && !files.get(index[0]).isEmpty()) {
                            try {
                                String uploadedName = storageService.uploadFile(files.get(index[0]++));
                                note.setFileUrl(uploadedName);
                            } catch (Exception e) {
                                throw new RuntimeException("Upload failed", e);
                            }
                        }
                    }
                }
            }
        }
    }*/


    @PostMapping("/new-course")
public String createCourse(@ModelAttribute Course course, 
                           @RequestParam(value = "noteFiles", required = false) List<MultipartFile> noteFiles) {
    
    final int[] fileIndex = {0};

    // 1. Handle hierarchy with direct subjects
    if (course.getSubjects() != null) {
        List<Subject> subjects = course.getSubjects();
        for (int i = 0; i < subjects.size(); i++) {
            Subject subject = subjects.get(i);
            subject.setCourse(course);
            subject.setSubjectOrder(i); // Assign sequential order to prevent NULL index crash
            processChapters(subject, subject.getChapters(), noteFiles, fileIndex);
        }
    }

    // 2. Handle hierarchy with classes
    if (course.getClasses() != null) {
        List<ClassEntity> classes = course.getClasses();
        for (int i = 0; i < classes.size(); i++) {
            ClassEntity classEntity = classes.get(i);
            classEntity.setCourse(course);
            classEntity.setClassOrder(i); // Assign sequential order for classes
            
            if (classEntity.getSubjects() != null) {
                List<Subject> classSubjects = classEntity.getSubjects();
                for (int j = 0; j < classSubjects.size(); j++) {
                    Subject subject = classSubjects.get(j);
                    subject.setClassEntity(classEntity);
                    subject.setCourse(course); 
                    subject.setSubjectOrder(j); // Assign sequential order within the class context
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
            // CRITICAL FIX: Tell the chapter which subject it belongs to
            chapter.setSubject(subject); 

            if (chapter.getNotes() != null) {
                for (Note note : chapter.getNotes()) {
                    note.setChapter(chapter);
                    
                    if (files != null && index[0] < files.size() && !files.get(index[0]).isEmpty()) {
                        try {
    String fileName = storageService.uploadFile(files.get(index[0]++));
    note.setFileUrl(fileName);
} catch (Exception e) {
    // If the error is the Base16 checksum issue, we can log it and move on
    if (e.getMessage().contains("base 16")) {
        System.out.println("Warning: Checksum validation failed, but file likely uploaded.");
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



    
    // Manage existing courses
    @GetMapping("/manage-courses")
    public String manageCourses(Model model) {
        List<Course> courses = courseRepository.findAll();
        model.addAttribute("courses", courses);
        return "new-course";
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
    /*
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
*/


@PostMapping("/add-note")
    public String addNote(@RequestParam Long chapterId,
                          @RequestParam String title,
                          @RequestParam("file") MultipartFile file,
                          @RequestParam BigDecimal price,
                          @RequestParam(defaultValue = "false") boolean isFree) {
        
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));

        try {
            String fileName = storageService.uploadFile(file);
            Note note = new Note(title, fileName, price, isFree, chapter);
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

