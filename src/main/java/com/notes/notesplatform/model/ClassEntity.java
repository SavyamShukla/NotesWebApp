package com.notes.notesplatform.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class ClassEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean deleted= false;
    private String name;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonBackReference("course-classes")
    private Course course;

    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("class-subject")
    private List<Subject> subjects;

    // Getters and Setters
     public ClassEntity() {}

    public ClassEntity(String name, Course course) {
        this.name = name;
        this.course = course;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public List<Subject> getSubjects() {
    return subjects;
}

public void setSubjects(List<Subject> subjects) {
    this.subjects = subjects;
}

public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted){
        this.deleted= deleted;
    }
}