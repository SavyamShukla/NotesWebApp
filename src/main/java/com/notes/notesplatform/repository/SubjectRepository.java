package com.notes.notesplatform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.notes.notesplatform.model.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    List<Subject> findByClassEntityIdAndDeletedFalse(Long classId);
    List<Subject> findByDeletedTrue();
    List<Subject> findByCourse_IdAndDeletedFalse(Long courseId);
}