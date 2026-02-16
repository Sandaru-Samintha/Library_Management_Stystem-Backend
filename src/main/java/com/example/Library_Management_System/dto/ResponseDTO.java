package com.example.Library_Management_System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseDTO {
    private String code;
    private String message;
    private Object content;
    private String jwtToken;
}