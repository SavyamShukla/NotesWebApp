package com.notes.notesplatform.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean deleted = false;
    private String name;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable= true)
    @JsonBackReference
    private ClassEntity classEntity;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable= false)
    @JsonBackReference
    private Course course;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Chapter> chapters;

    // Getters and Setters

    public Subject() {}

    public Subject(String name, ClassEntity classEntity) {
        this.name = name;
        this.classEntity = classEntity;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ClassEntity getClassEntity() { return classEntity; }
    public void setClassEntity(ClassEntity classEntity) { this.classEntity = classEntity; }

    public List<Chapter> getChapters() {
    return chapters;
}

public void setChapters(List<Chapter> chapters) {
    this.chapters = chapters;
}

     public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted){
        this.deleted= deleted;
    }

    public Course getCourse() {
    return course;
}

public void setCourse(Course course) {
    this.course = course;
}
}