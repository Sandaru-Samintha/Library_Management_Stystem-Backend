package com.example.Library_Management_System.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @Column(nullable = false)
    private String bookTitle;

    @Column(nullable = false)
    private String bookAuthor;

    private String bookGenre;
    private String bookIsbn;
    private String bookPublisher;
    private Integer bookPublicationYear;
    private Double bookPrice;
    private String bookDescription;
    private String shelfLocation;
    private Integer totalCopies = 1;
    private Integer availableCopies = 1;

    @Column(nullable = false)
    private Boolean bookAvailable = true;

    @Column(length = 500)
    private String bookImageUrl;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @JsonIgnore  // THIS IS THE KEY FIX - prevents infinite recursion
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<BorrowRecord> borrowRecords;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
}