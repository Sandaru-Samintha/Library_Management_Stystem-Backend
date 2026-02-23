package com.example.Library_Management_System.controller;

import com.example.Library_Management_System.dto.ResponseDTO;
import com.example.Library_Management_System.entity.BorrowStatus;
import com.example.Library_Management_System.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrow")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    // ==================== MEMBER ENDPOINTS ====================

    /**
     * Borrow a book
     */
    @PostMapping("/book/{bookId}")
    @PreAuthorize("hasAuthority('MEMBER')")
    public ResponseEntity<ResponseDTO> borrowBook(@PathVariable Long bookId) {
        ResponseDTO responseDTO = borrowService.borrowBook(bookId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * Return a borrowed book

     */
    @PutMapping("/return/{borrowId}")
    @PreAuthorize("hasAuthority('MEMBER')")
    public ResponseEntity<ResponseDTO> returnBook(@PathVariable Long borrowId) {
        ResponseDTO responseDTO = borrowService.returnBook(borrowId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * Get my borrowed books

     */
    @GetMapping("/my-books")
    @PreAuthorize("hasAuthority('MEMBER')")
    public ResponseEntity<ResponseDTO> getMyBorrowedBooks() {
        ResponseDTO responseDTO = borrowService.getMyBorrowedBooks();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     *  Get my complete borrow history

     */
    @GetMapping("/my-history")
    @PreAuthorize("hasAuthority('MEMBER')")
    public ResponseEntity<ResponseDTO> getMyBorrowHistory() {
        ResponseDTO responseDTO = borrowService.getMyBorrowHistory();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * Get current borrowed books

     */
    @GetMapping("/current")
    @PreAuthorize("hasAuthority('MEMBER')")
    public ResponseEntity<ResponseDTO> getCurrentBorrows() {
        ResponseDTO responseDTO = borrowService.getCurrentBorrows();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * Check if member has overdue books

     */
    @GetMapping("/overdue")
    @PreAuthorize("hasAuthority('MEMBER')")
    public ResponseEntity<ResponseDTO> checkMyOverdueBooks() {
        ResponseDTO responseDTO = borrowService.checkMyOverdueBooks();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    // ==================== ADMIN ENDPOINTS ====================

    /**
     * Get all borrow records

     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getAllBorrowRecords() {
        ResponseDTO responseDTO = borrowService.getAllBorrowRecords();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     *  Get borrows by status

     */
    @GetMapping("/admin/status/{status}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getBorrowsByStatus(@PathVariable BorrowStatus status) {
        ResponseDTO responseDTO = borrowService.getBorrowsByStatus(status);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * Get borrow history for a specific member

     */
    @GetMapping("/admin/member/{memberId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getMemberBorrowHistory(@PathVariable Long memberId) {
        ResponseDTO responseDTO = borrowService.getMemberBorrowHistory(memberId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * Get borrow history for a specific book

     */
    @GetMapping("/admin/book/{bookId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getBookBorrowHistory(@PathVariable Long bookId) {
        ResponseDTO responseDTO = borrowService.getBookBorrowHistory(bookId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * Extend due date for a borrow

     */
    @PutMapping("/admin/{borrowId}/extend")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> extendDueDate(
            @PathVariable Long borrowId,
            @RequestParam(defaultValue = "7") int additionalDays) {
        ResponseDTO responseDTO = borrowService.extendDueDate(borrowId, additionalDays);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * Check and update overdue books

     */
    @PostMapping("/admin/check-overdue")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> checkOverdueBooks() {
        ResponseDTO responseDTO = borrowService.checkOverdueBooks();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     *  Get all active borrows

     */
    @GetMapping("/admin/active")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getActiveBorrows() {
        ResponseDTO responseDTO = borrowService.getActiveBorrows();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * Get all overdue books

     */
    @GetMapping("/admin/overdue")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getOverdueBooks() {
        ResponseDTO responseDTO = borrowService.getOverdueBooks();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     *  Get books due for return today

     */
    @GetMapping("/admin/today-returns")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getTodayReturns() {
        ResponseDTO responseDTO = borrowService.getTodayReturns();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     *  Get borrowing statistics

     */
    @GetMapping("/admin/stats")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getBorrowingStats() {
        ResponseDTO responseDTO = borrowService.getBorrowingStats();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}