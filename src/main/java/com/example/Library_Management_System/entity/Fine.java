package com.example.Library_Management_System.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "fine")
public class Fine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fineId;

    @ManyToOne
    @JoinColumn(name = "member_id",nullable = false)
    private Member member;

    @OneToOne
    @JoinColumn(name = "borrow_id",nullable = false)
    private BorrowRecord borrowRecord;

    @Column(nullable = false)
    private Double amount;

    private LocalDate fineDate;

    @Enumerated(EnumType.STRING)
    private FineStatus status = FineStatus.UNPAID;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist
    protected void onCreate(){
        fineDate = LocalDate.now();
        createdDate=LocalDateTime.now();
        updatedDate = LocalDateTime.now();

        //calculate fine if overdue
        if(borrowRecord != null && borrowRecord.getReturnDate() ==null ){
            LocalDate dueDate = borrowRecord.getDueDate();
            LocalDate today = LocalDate.now();

            if(today.isAfter(dueDate)){
                long daysOverdue = ChronoUnit.DAYS.between(dueDate,today);
                this.amount = daysOverdue * 10.0; // Rs .10 per day
            }
        }
    }
    @PrePersist
    protected void  onUpdate(){
        updatedDate = LocalDateTime.now();
    }


}
