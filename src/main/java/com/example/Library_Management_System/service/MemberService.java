package com.example.Library_Management_System.service;

import com.example.Library_Management_System.dto.MemberDTO;
import com.example.Library_Management_System.dto.ResponseDTO;
import com.example.Library_Management_System.entity.Member;
import com.example.Library_Management_System.repository.MemberRepository;
import com.example.Library_Management_System.util.VarList;
import com.example.Library_Management_System.util.FileUploadUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @Autowired
    private ModelMapper modelMapper;

    public ResponseDTO getMemberProfile() {
        try {
            String email = getCurrentUserEmail();
            Optional<Member> memberOpt = memberRepository.findByMemEmail(email);

            if (memberOpt.isPresent()) {
                MemberDTO memberDTO = modelMapper.map(memberOpt.get(), MemberDTO.class);
                return new ResponseDTO(VarList.RSP_SUCCESS,
                        "Profile retrieved successfully", memberDTO, null);
            } else {
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND,
                        "Member not found", null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(VarList.RSP_ERROR,
                    "Failed to retrieve profile: " + e.getMessage(), null, null);
        }
    }

    public ResponseDTO updateMemberProfile(MemberDTO memberDTO) {
        try {
            String email = getCurrentUserEmail();
            Optional<Member> memberOpt = memberRepository.findByMemEmail(email);

            if (!memberOpt.isPresent()) {
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND,
                        "Member not found", null, null);
            }

            Member member = memberOpt.get();

            // Store the current active status
            boolean currentActiveStatus = member.isActive();

            // MANUALLY update fields to preserve active status
            // This is safer than using ModelMapper which might overwrite fields

            // Update basic info if provided
            if (memberDTO.getMemFullName() != null && !memberDTO.getMemFullName().isEmpty()) {
                member.setMemFullName(memberDTO.getMemFullName());
            }

            if (memberDTO.getMemPhoneNumber() != null) {
                member.setMemPhoneNumber(memberDTO.getMemPhoneNumber());
            }

            if (memberDTO.getMemAddress() != null) {
                member.setMemAddress(memberDTO.getMemAddress());
            }

            // Update password if provided
            if (memberDTO.getMemPassword() != null && !memberDTO.getMemPassword().isEmpty()) {
                member.setMemPassword(passwordEncoder.encode(memberDTO.getMemPassword()));
            }

            // Handle profile image upload
            if (memberDTO.getProfileImage() != null && !memberDTO.getProfileImage().isEmpty()) {
                // Delete old image if exists
                if (member.getProfileImageUrl() != null && !member.getProfileImageUrl().isEmpty()) {
                    try {
                        fileUploadUtil.deleteImage(member.getProfileImageUrl());
                    } catch (Exception e) {
                        System.err.println("Failed to delete old image: " + e.getMessage());
                    }
                }
                String imageUrl = fileUploadUtil.saveMemberImage(memberDTO.getProfileImage());
                member.setProfileImageUrl(imageUrl);
            }

            // CRITICAL: Restore the active status (prevents accidental deactivation)
            member.setActive(currentActiveStatus);

            Member updatedMember = memberRepository.save(member);
            MemberDTO responseDTO = modelMapper.map(updatedMember, MemberDTO.class);

            return new ResponseDTO(VarList.RSP_SUCCESS,
                    "Profile updated successfully", responseDTO, null);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(VarList.RSP_ERROR,
                    "Failed to update profile: " + e.getMessage(), null, null);
        }
    }

    private String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }
}