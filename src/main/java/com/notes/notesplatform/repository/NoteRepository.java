package com.notes.notesplatform.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.notes.notesplatform.model.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByChapterIdAndDeletedFalse(Long chapterId);

    List<Note> findByDeletedTrue();

    @Query("""
            SELECT new map(
                n.id as id,
                n.title as title,
                n.fileUrl as fileUrl,
                n.price as price,
                n.isFree as isFree,
                CONCAT(c.name, ' > ', ce.name, ' > ', s.name, ' > ', ch.name, ' > ', n.title) as fullPath
            )
            FROM Note n
            LEFT JOIN n.chapter ch
            LEFT JOIN ch.subject s
            LEFT JOIN s.classEntity ce
            LEFT JOIN ce.course c
            WHERE n.deleted = true
            """)
    List<Map<String, Object>> findDeletedNotesWithFullPath();

}