/*package com.notes.notesplatform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.notes.notesplatform.model.Note;
import com.notes.notesplatform.model.PurchasedNote;
import com.notes.notesplatform.model.User;

public interface PurchasedNoteRepository extends JpaRepository<PurchasedNote, Long> {
    List<PurchasedNote> findByUser(User user);
    boolean existsByUserAndNote(User user, Note note);
}*/




package com.notes.notesplatform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.notes.notesplatform.model.Note;
import com.notes.notesplatform.model.PurchasedNote;
import com.notes.notesplatform.model.User;

public interface PurchasedNoteRepository extends JpaRepository<PurchasedNote, Long> {

    // Old method - This is the one causing the N+1 issue if used on the dashboard
    List<PurchasedNote> findByUser(User user);
    
    // New, optimized method to solve the N+1 problem
    @Query("SELECT pn FROM PurchasedNote pn JOIN FETCH pn.note WHERE pn.user = :user")
    List<PurchasedNote> findByUserWithNotes(@Param("user") User user);
    
    // This method is also a source of N+1 on the notes page, as discussed previously
    boolean existsByUserAndNote(User user, Note note);
}