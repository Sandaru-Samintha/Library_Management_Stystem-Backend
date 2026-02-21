package com.example.Library_Management_System.repository;

import com.example.Library_Management_System.entity.Fine;
import com.example.Library_Management_System.entity.FineStatus;
import com.example.Library_Management_System.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FineRepository extends JpaRepository<Fine, Long> {

    List<Fine> findByMember(Member member);

    List<Fine> findByStatus(FineStatus status);

    List<Fine> findByMemberAndStatus(Member member, FineStatus status);

    // FIXED: Changed from findBorrowRecord_BorrowId to findByBorrowRecord_BorrowId
    Fine findByBorrowRecord_BorrowId(Long borrowId);

    // Alternative using @Query
    @Query("SELECT f FROM Fine f WHERE f.borrowRecord.borrowId = :borrowId")
    Fine findFineByBorrowId(@Param("borrowId") Long borrowId);

    List<Fine> findByMember_MemId(Long memberId);
}