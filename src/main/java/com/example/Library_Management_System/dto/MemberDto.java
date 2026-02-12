package com.example.Library_Management_System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MemberDto {


    private Long memberID;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private LocalDate membershipDate;
    private boolean active;

}
