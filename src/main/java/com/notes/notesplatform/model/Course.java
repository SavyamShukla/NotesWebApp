/*package com.notes.notesplatform.model;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.hibernate.annotations.BatchSize;

@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean deleted = false;
    private String name;
    private String description;
   
 
    @BatchSize(size = 10)
    @OneToMany(mappedBy = "course", cascade= CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("course-subject")
    private Set<Subject> subjects = new HashSet<>();
    
    @BatchSize(size = 10)
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("course-classes")
    private Set<ClassEntity> classes = new HashSet<>();


    // Getters and Setters
     public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public  Set<ClassEntity> getClasses() {return classes;}
    public void setClasses(Set<ClassEntity> classes) {this.classes = classes;}

    public boolean isDeleted() {return deleted;}
    public void setDeleted(boolean deleted){this.deleted= deleted;}

    public Set<Subject> getSubjects() {return subjects;}
    public void setSubjects(Set<Subject> subjects) {this.subjects = subjects;}
    
}*/

package com.notes.notesplatform.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.HashSet;
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
    private Set<Subject> subjects = new HashSet<>();

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ClassEntity> classes = new HashSet<>();

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

    public Set<Subject> getSubjects() { 
        return subjects; 
    }

    public void setSubjects(Set<Subject> subjects) { 
        this.subjects = subjects; 
    }

    public Set<ClassEntity> getClasses() { 
        return classes; 
    }

    public void setClasses(Set<ClassEntity> classes) { 
        this.classes = classes; 
    }
}
