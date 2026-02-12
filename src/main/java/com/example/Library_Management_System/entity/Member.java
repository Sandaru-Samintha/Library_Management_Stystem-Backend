package com.example.Library_Management_System.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "Member" ,uniqueConstraints = @UniqueConstraint(columnNames = "memEmail"))
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memID;

    private String memFullName;
    private String memEmail;
    private String memPhoneNumber;
    private String memAddress;

    private LocalDate membershipDate;
    private boolean active;
}
