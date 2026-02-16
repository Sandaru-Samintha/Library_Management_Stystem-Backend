package com.example.Library_Management_System.repository;

import com.example.Library_Management_System.entity.Book;
import com.example.Library_Management_System.entity.BorrowRecord;
import com.example.Library_Management_System.entity.BorrowStatus;
import com.example.Library_Management_System.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord,Long> {
    List<BorrowRecord>findByMember(Member member);
    List<BorrowRecord>findByBook(Book book);

    List<BorrowRecord>findByStatus(BorrowStatus status);

    List<BorrowRecord>findByMemberAndStatus(Member member,BorrowStatus status);

    @Query("SELECT br FROM BorrowRecord br WHERE br.member.memId = :memberId AND br.status = 'BORROWED'")
    List<BorrowRecord> findCurrentBorrowsByMember(@Param("memberId") Long memberId);

    @Query("SELECT br FROM BorrowRecord br WHERE br.dueDate < :currentDate AND br.status = 'BORROWED'")
    List<BorrowRecord>findOverdueBorrows(@Param("CurrentDate")LocalDate currentDate);

    boolean existsByMemberAndBookAndStatus(Member member,Book book,BorrowStatus status);
}

