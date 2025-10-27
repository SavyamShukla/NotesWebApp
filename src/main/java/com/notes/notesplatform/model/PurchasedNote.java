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
}



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
    public void setRazorpayPaymentId(String paymentId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setRazorpayPaymentId'");
    }
    public void setRazorpayOrderId(String orderId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setRazorpayOrderId'");
    }
}*/


package com.notes.notesplatform.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchased_note")
public class PurchasedNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    private LocalDateTime purchasedAt;

    // Add fields to store payment details for auditing and reference
    @Column(name = "razorpay_payment_id")
    private String razorpayPaymentId;

    @Column(name = "razorpay_order_id")
    private String razorpayOrderId;

    @Column(name = "purchased")
    private boolean purchased;


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

    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    // Fully implemented setter
    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    // Fully implemented setter
    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }
}
