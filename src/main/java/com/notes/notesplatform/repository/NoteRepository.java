package com.notes.notesplatform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.notes.notesplatform.model.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByChapterIdAndDeletedFalse(Long chapterId);
   

    @EntityGraph(attributePaths = {
    "chapter",
    "chapter.subject",
    "chapter.subject.classEntity",
    "chapter.subject.classEntity.course"
})
List<Note> findByDeletedTrue();

    
}