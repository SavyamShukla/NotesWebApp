package com.notes.notesplatform.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.annotations.BatchSize;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean deleted = false;

    private String name;
    private String description;

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subject> subjects = new ArrayList<>();

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClassEntity> classes = new ArrayList<>();

    // Getters and Setters

    public Long getId() { 
        return id; 
    }

    public void setId(Long id) { 
        this.id = id; 
    }

    public boolean isDeleted() { 
        return deleted; 
    }

    public void setDeleted(boolean deleted) { 
        this.deleted = deleted; 
    }

    public String getName() { 
        return name; 
    }

    public void setName(String name) { 
        this.name = name; 
    }

    public String getDescription() { 
        return description; 
    }

    public void setDescription(String description) { 
        this.description = description; 
    }

    public List<Subject> getSubjects() { 
        return subjects; 
    }

    public void setSubjects(List<Subject> subjects) { 
        this.subjects = subjects; 
    }

    public List<ClassEntity> getClasses() { 
        return classes; 
    }

    public void setClasses(List<ClassEntity> classes) { 
        this.classes = classes; 
    }
}
