package com.example.Library_Management_System.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "member",
        uniqueConstraints = @UniqueConstraint(columnNames = "memEmail"))
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memId;

    @Column(nullable = false)
    private String memFullName;

    @Column(nullable = false, unique = true)
    private String memEmail;

    private String memPhoneNumber;
    private String memAddress;

    @Column(nullable = false)
    private String memPassword;

    private LocalDate membershipDate;
    private boolean active = true;
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private Role role = Role.MEMBER;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    //@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    //private List<BorrowRecord> borrowRecords;

   // @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    //private List<Fine> fines;

    @PrePersist
    protected void onCreate() {
        membershipDate = LocalDate.now();
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
}