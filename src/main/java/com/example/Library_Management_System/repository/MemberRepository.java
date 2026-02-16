package com.example.Library_Management_System.repository;

import com.example.Library_Management_System.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemEmail(String email);

    boolean existsByMemEmail(String email);

    Optional<Member> findByMemEmailAndActiveTrue(String email);
}