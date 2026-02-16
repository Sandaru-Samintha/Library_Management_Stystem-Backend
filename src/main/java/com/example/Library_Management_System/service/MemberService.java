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

            // Map DTO to existing entity (excluding password & image)
            modelMapper.map(memberDTO, member);

            // Update password if provided
            if (memberDTO.getMemPassword() != null &&
                    !memberDTO.getMemPassword().isEmpty()) {
                member.setMemPassword(
                        passwordEncoder.encode(memberDTO.getMemPassword()));
            }

            // Handle profile image upload
            if (memberDTO.getProfileImage() != null &&
                    !memberDTO.getProfileImage().isEmpty()) {
                String imageUrl =
                        fileUploadUtil.saveMemberImage(memberDTO.getProfileImage());
                member.setProfileImageUrl(imageUrl);
            }

            Member updatedMember = memberRepository.save(member);
            MemberDTO responseDTO = modelMapper.map(updatedMember, MemberDTO.class);

            return new ResponseDTO(VarList.RSP_SUCCESS,
                    "Profile updated successfully", responseDTO, null);

        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR,
                    "Failed to update profile: " + e.getMessage(), null, null);
        }
    }

    private String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }
}
