package com.example.Library_Management_System.dto;

import com.example.Library_Management_System.entity.BorrowStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BorrowRecordDTO {

    // Borrow Record fields
    private Long borrowId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private BorrowStatus status;
    private Double fineAmount;

    // Book fields
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookGenre;
    private String bookIsbn;

    // Member fields
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private String memberPhoneNumber;

    // Admin fields
    private Long adminId;
    private String adminName;
    private String adminEmail;

    // Audit fields
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}