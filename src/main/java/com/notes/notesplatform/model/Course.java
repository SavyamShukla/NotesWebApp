package com.notes.notesplatform.model;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean deleted = false;
    private String name;
    private String description;
    @OneToMany(mappedBy = "course", cascade= CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Subject> subjects;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ClassEntity> classes;



    // Getters and Setters
     public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<ClassEntity> getClasses() {return classes;}
    public void setClasses(List<ClassEntity> classes) {this.classes = classes;}

    public boolean isDeleted() {return deleted;}
    public void setDeleted(boolean deleted){this.deleted= deleted;}

    public List<Subject> getSubjects() {return subjects;}
    public void setSubjects(List<Subject> subjects) {this.subjects = subjects;}
    
}