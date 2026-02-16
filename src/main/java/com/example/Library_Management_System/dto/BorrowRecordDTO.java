package com.example.Library_Management_System.dto;

import com.example.Library_Management_System.entity.BorrowStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BorrowRecordDTO {
    private Long borrowId;
    private Long bookId;
    private String bookTitle;
    private Long memberId;
    private String memberName;
    private Long adminId;
    private String adminName;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private BorrowStatus status;
    private Double fineAmount;
}