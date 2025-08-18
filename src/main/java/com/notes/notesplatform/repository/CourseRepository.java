package com.notes.notesplatform.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.notes.notesplatform.model.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
     List<Course> findByDeletedFalse();
     List<Course> findByDeletedTrue();
     
     
}