package com.example.Library_Management_System.service;

import com.example.Library_Management_System.dto.LoginDTO;
import com.example.Library_Management_System.dto.MemberDTO;
import com.example.Library_Management_System.dto.ResponseDTO;
import com.example.Library_Management_System.entity.Member;
import com.example.Library_Management_System.entity.Role;
import com.example.Library_Management_System.repository.MemberRepository;
import com.example.Library_Management_System.security.JwtUtil;
import com.example.Library_Management_System.util.VarList;
import com.example.Library_Management_System.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class AuthService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    public ResponseDTO registerMember(MemberDTO memberDTO) {
        try {
            if (memberRepository.existsByMemEmail(memberDTO.getMemEmail())) {
                return new ResponseDTO(VarList.RSP_DUPLICATED, "Email already exists", null, null);
            }

            Member member = new Member();
            member.setMemFullName(memberDTO.getMemFullName());
            member.setMemEmail(memberDTO.getMemEmail());
            member.setMemPhoneNumber(memberDTO.getMemPhoneNumber());
            member.setMemAddress(memberDTO.getMemAddress());
            member.setMemPassword(passwordEncoder.encode(memberDTO.getMemPassword()));
            member.setRole(Role.MEMBER);
            member.setActive(true);

            // Handle profile image upload
            if (memberDTO.getProfileImage() != null && !memberDTO.getProfileImage().isEmpty()) {
                String imageUrl = fileUploadUtil.saveMemberImage(memberDTO.getProfileImage());
                member.setProfileImageUrl(imageUrl);
            }

            Member savedMember = memberRepository.save(member);

            String token = jwtUtil.generateToken(savedMember.getMemEmail(), savedMember.getRole().name());

            return new ResponseDTO(VarList.RSP_SUCCESS, "Registration successful", savedMember, token);

        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Registration failed: " + e.getMessage(), null, null);
        }
    }

    public ResponseDTO login(LoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails.getUsername(), userDetails.getAuthorities().iterator().next().getAuthority());

            Object user = null;
            if (loginDTO.getRole().equals("MEMBER")) {
                user = memberRepository.findByMemEmail(loginDTO.getEmail()).orElse(null);
            }

            return new ResponseDTO(VarList.RSP_SUCCESS, "Login successful", user, token);

        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Login failed: Invalid credentials", null, null);
        }
    }
}