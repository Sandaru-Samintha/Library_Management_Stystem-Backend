package com.example.Library_Management_System.controller;

import com.example.Library_Management_System.dto.AdminDTO;
import com.example.Library_Management_System.dto.LoginDTO;
import com.example.Library_Management_System.dto.MemberDTO;
import com.example.Library_Management_System.dto.ResponseDTO;
import com.example.Library_Management_System.service.AdminService;
import com.example.Library_Management_System.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AdminService adminService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@ModelAttribute MemberDTO memberDTO) {
        ResponseDTO responseDTO = authService.registerMember(memberDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody LoginDTO loginDTO) {
        ResponseDTO responseDTO = authService.login(loginDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    // Public endpoint to create first admin (you can remove this after creating first admin)
    @PostMapping("/register-admin")
    public ResponseEntity<ResponseDTO> registerAdmin(@ModelAttribute AdminDTO adminDTO) {
        ResponseDTO responseDTO = adminService.createAdmin(adminDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}