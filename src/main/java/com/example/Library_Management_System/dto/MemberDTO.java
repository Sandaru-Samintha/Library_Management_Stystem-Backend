package com.example.Library_Management_System.dto;

import com.example.Library_Management_System.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MemberDTO {
    private Long memId;
    private String memFullName;
    private String memEmail;
    private String memPhoneNumber;
    private String memAddress;
    private String memPassword;
    private boolean active;
    private Role role;
    private LocalDate membershipDate;
    private String profileImageUrl;
    private MultipartFile profileImage;
}