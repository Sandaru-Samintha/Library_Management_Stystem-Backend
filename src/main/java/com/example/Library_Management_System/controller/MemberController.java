package com.example.Library_Management_System.controller;

import com.example.Library_Management_System.dto.MemberDTO;
import com.example.Library_Management_System.dto.ResponseDTO;
import com.example.Library_Management_System.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@CrossOrigin
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('MEMBER')")
    public ResponseEntity<ResponseDTO> getProfile() {
        ResponseDTO responseDTO = memberService.getMemberProfile();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PutMapping("/profile/update")
    @PreAuthorize("hasAuthority('MEMBER')")
    public ResponseEntity<ResponseDTO> updateProfile(@ModelAttribute MemberDTO memberDTO) {
        ResponseDTO responseDTO = memberService.updateMemberProfile(memberDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}