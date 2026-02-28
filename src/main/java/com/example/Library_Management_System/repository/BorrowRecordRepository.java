package com.example.Library_Management_System.repository;

import com.example.Library_Management_System.entity.Book;
import com.example.Library_Management_System.entity.BorrowRecord;
import com.example.Library_Management_System.entity.BorrowStatus;
import com.example.Library_Management_System.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    // Find by Member
    List<BorrowRecord> findByMember(Member member);

    // Find by Book
    List<BorrowRecord> findByBook(Book book);

    // Find by Status
    List<BorrowRecord> findByStatus(BorrowStatus status);

    // Find by Member and Status
    List<BorrowRecord> findByMemberAndStatus(Member member, BorrowStatus status);

    // Find current borrows by member ID
    @Query("SELECT br FROM BorrowRecord br WHERE br.member.memId = :memberId AND br.status = 'BORROWED'")
    List<BorrowRecord> findCurrentBorrowsByMember(@Param("memberId") Long memberId);

    // Find overdue borrows
    @Query("SELECT br FROM BorrowRecord br WHERE br.dueDate < :currentDate AND br.status = 'BORROWED'")
    List<BorrowRecord> findOverdueBorrows(@Param("currentDate") LocalDate currentDate);

    // Check if exists by Member, Book and Status
    boolean existsByMemberAndBookAndStatus(Member member, Book book, BorrowStatus status);

    // Find by due date before and status (for overdue check)
    List<BorrowRecord> findByDueDateBeforeAndStatus(LocalDate dueDate, BorrowStatus status);

    // NEW: Find by exact due date and status (for today's returns)
    List<BorrowRecord> findByDueDateAndStatus(LocalDate dueDate, BorrowStatus status);

    // Alternative using @Query
    @Query("SELECT br FROM BorrowRecord br WHERE br.dueDate = :dueDate AND br.status = :status")
    List<BorrowRecord> findBooksDueOnDate(@Param("dueDate") LocalDate dueDate, @Param("status") BorrowStatus status);
}