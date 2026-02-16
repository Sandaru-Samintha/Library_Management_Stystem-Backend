package com.example.Library_Management_System.dto;

import com.example.Library_Management_System.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AdminDTO {
    private Long adminId;
    private String adminFullName;
    private String adminEmail;
    private String adminPhoneNumber;
    private String adminPassword;
    private String adminDepartment;
    private Role role;
    private String profileUrl;
    private MultipartFile profileImage;
}
