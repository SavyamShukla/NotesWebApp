package com.notes.notesplatform.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean deleted;
    private String title;
    private String fileUrl;

    @Column(precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    private boolean isFree;

    @ManyToOne
    @JoinColumn(name = "chapter_id")
    @JsonBackReference("chapter-note")
    private Chapter chapter;

    public Note() {
    }

    public Note(String title, String fileUrl, BigDecimal price, boolean isFree, Chapter chapter) {
        this.title = title;
        this.fileUrl = fileUrl;
        this.price = price;
        this.isFree = isFree;
        this.chapter = chapter;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
