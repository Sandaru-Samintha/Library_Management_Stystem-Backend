package com.example.Library_Management_System.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "admin" ,uniqueConstraints = @UniqueConstraint(columnNames = "adminEmail"))
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId;

    @Column(nullable = false)
    private String adminFullName;

    @Column(nullable = false,unique = true)
    private String adminEmail;

    private String adminPhoneNumber;

    @Column(nullable = false)
    private String adminPassword;

    private String adminDepartment;
    private String profileImageUrl;


    @Enumerated(EnumType.STRING)
    private Role role = Role.ADMIN;

    private LocalDateTime createdDate;
    private  LocalDateTime updatedDate;

    @PreUpdate
    protected  void onCreate(){
        createdDate =LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }
    @PreUpdate
    protected  void onUpdate(){
        updatedDate = LocalDateTime.now();
    }



}
