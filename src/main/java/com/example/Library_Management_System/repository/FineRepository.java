package com.example.Library_Management_System.repository;

import com.example.Library_Management_System.entity.Fine;
import com.example.Library_Management_System.entity.FineStatus;
import com.example.Library_Management_System.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FineRepository extends JpaRepository<Fine , Long> {
    List<Fine> findByMember(Member member);
    List<Fine> findByStatus(FineStatus status);

    List<Fine> findByMemberAndStatus(Member member,FineStatus status);

    Fine findBorrowRecord_BorrowId(Long borrowId);
}
