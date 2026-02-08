package com.notes.notesplatform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.notes.notesplatform.model.Chapter;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    List<Chapter> findBySubjectIdAndDeletedFalse(Long subjectId);
   

    @EntityGraph(attributePaths = {
    "subject",
    "subject.classEntity",
    "subject.classEntity.course"
})
List<Chapter> findByDeletedTrue();

}