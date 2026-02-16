package com.example.Library_Management_System.security;

import com.example.Library_Management_System.entity.Admin;
import com.example.Library_Management_System.entity.Member;
import com.example.Library_Management_System.repository.AdminRepository;
import com.example.Library_Management_System.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Try to find as Member first
        Member member = memberRepository.findByMemEmail(email).orElse(null);
        if (member != null && member.isActive()) {
            return new User(
                    member.getMemEmail(),
                    member.getMemPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority(member.getRole().name()))
            );
        }

        // Then try as Admin
        Admin admin = adminRepository.findByAdminEmail(email).orElse(null);
        if (admin != null) {
            return new User(
                    admin.getAdminEmail(),
                    admin.getAdminPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority(admin.getRole().name()))
            );
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}