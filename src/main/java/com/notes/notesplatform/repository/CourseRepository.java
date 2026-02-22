package com.notes.notesplatform.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.notes.notesplatform.model.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @EntityGraph(attributePaths = { "subjects", "classes" })
    List<Course> findAllByDeletedFalse();

    List<Course> findByDeletedFalse();

    @EntityGraph(attributePaths = { "subjects", "classes" })
    List<Course> findByDeletedTrue();

    @EntityGraph(attributePaths = { "subjects", "classes" })
    Optional<Course> findByIdAndDeletedFalse(Long id);
}
