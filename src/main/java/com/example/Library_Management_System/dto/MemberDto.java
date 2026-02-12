package com.example.Library_Management_System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MemberDto {

    private Long memID;
    private String memFullName;
    private String memEmail;
    private String memPhoneNumber;
    private String memAddress;
    private LocalDate membershipDate;
    private boolean active;

}
