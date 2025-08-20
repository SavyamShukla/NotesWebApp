/*package com.notes.notesplatform.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
public class PurchasedNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Note note;

    private boolean purchased;

    private LocalDateTime purchasedAt;

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public LocalDateTime getPurchasedAt() {
        return purchasedAt;
    }

    public void setPurchasedAt(LocalDateTime purchasedAt) {
        this.purchasedAt = purchasedAt;
    }
}*/



package com.notes.notesplatform.model;

import jakarta.persistence.*; // Make sure all jakarta.persistence imports are present
import java.time.LocalDateTime;

@Entity
@Table(name = "purchased_note") // <-- ADD THIS LINE
public class PurchasedNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id") // <-- ADD THIS for clarity
    private User user;

    @ManyToOne
    @JoinColumn(name = "note_id") // <-- ADD THIS for clarity
    private Note note;

    private boolean purchased;

    private LocalDateTime purchasedAt;

    // --- Getters and Setters ---
    // (Your existing getters and setters are fine)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Note getNote() { return note; }
    public void setNote(Note note) { this.note = note; }
    public LocalDateTime getPurchasedAt() { return purchasedAt; }
    public void setPurchasedAt(LocalDateTime purchasedAt) { this.purchasedAt = purchasedAt; }
}