package com.example.Library_Management_System.repository;

import com.example.Library_Management_System.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member , Long> {
    boolean existByEmailIgnoreCase(String memEmail);
}
