package com.example.Library_Management_System.dto;

import com.example.Library_Management_System.entity.FineStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FineDTO {
    private Long fineId;
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private Long borrowId;
    private String bookTitle;
    private Double amount;
    private LocalDate fineDate;
    private FineStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}