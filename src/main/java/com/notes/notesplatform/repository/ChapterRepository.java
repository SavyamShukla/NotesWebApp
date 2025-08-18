package com.notes.notesplatform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.notes.notesplatform.model.Chapter;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    List<Chapter> findBySubjectIdAndDeletedFalse(Long subjectId);
    List<Chapter> findByDeletedTrue();
}