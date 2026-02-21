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
     * 1. Borrow a book
     * POST /api/borrow/book/{bookId}
     */
    @PostMapping("/book/{bookId}")
    @PreAuthorize("hasAuthority('MEMBER')")
    public ResponseEntity<ResponseDTO> borrowBook(@PathVariable Long bookId) {
        ResponseDTO responseDTO = borrowService.borrowBook(bookId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * 2. Return a borrowed book
     * PUT /api/borrow/return/{borrowId}
     */
    @PutMapping("/return/{borrowId}")
    @PreAuthorize("hasAuthority('MEMBER')")
    public ResponseEntity<ResponseDTO> returnBook(@PathVariable Long borrowId) {
        ResponseDTO responseDTO = borrowService.returnBook(borrowId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * 3. Get my borrowed books
     * GET /api/borrow/my-books
     */
    @GetMapping("/my-books")
    @PreAuthorize("hasAuthority('MEMBER')")
    public ResponseEntity<ResponseDTO> getMyBorrowedBooks() {
        ResponseDTO responseDTO = borrowService.getMyBorrowedBooks();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * 4. Get my complete borrow history
     * GET /api/borrow/my-history
     */
    @GetMapping("/my-history")
    @PreAuthorize("hasAuthority('MEMBER')")
    public ResponseEntity<ResponseDTO> getMyBorrowHistory() {
        ResponseDTO responseDTO = borrowService.getMyBorrowHistory();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * 5. Get current borrowed books
     * GET /api/borrow/current
     */
    @GetMapping("/current")
    @PreAuthorize("hasAuthority('MEMBER')")
    public ResponseEntity<ResponseDTO> getCurrentBorrows() {
        ResponseDTO responseDTO = borrowService.getCurrentBorrows();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * 6. Check if member has overdue books
     * GET /api/borrow/overdue
     */
    @GetMapping("/overdue")
    @PreAuthorize("hasAuthority('MEMBER')")
    public ResponseEntity<ResponseDTO> checkMyOverdueBooks() {
        ResponseDTO responseDTO = borrowService.checkMyOverdueBooks();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    // ==================== ADMIN ENDPOINTS ====================

    /**
     * 7. Get all borrow records
     * GET /api/borrow/admin/all
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getAllBorrowRecords() {
        ResponseDTO responseDTO = borrowService.getAllBorrowRecords();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * 8. Get borrows by status
     * GET /api/borrow/admin/status/{status}
     */
    @GetMapping("/admin/status/{status}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getBorrowsByStatus(@PathVariable BorrowStatus status) {
        ResponseDTO responseDTO = borrowService.getBorrowsByStatus(status);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * 9. Get borrow history for a specific member
     * GET /api/borrow/admin/member/{memberId}
     */
    @GetMapping("/admin/member/{memberId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getMemberBorrowHistory(@PathVariable Long memberId) {
        ResponseDTO responseDTO = borrowService.getMemberBorrowHistory(memberId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * 10. Get borrow history for a specific book
     * GET /api/borrow/admin/book/{bookId}
     */
    @GetMapping("/admin/book/{bookId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getBookBorrowHistory(@PathVariable Long bookId) {
        ResponseDTO responseDTO = borrowService.getBookBorrowHistory(bookId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * 11. Extend due date for a borrow
     * PUT /api/borrow/admin/{borrowId}/extend?additionalDays=7
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
     * 12. Check and update overdue books
     * POST /api/borrow/admin/check-overdue
     */
    @PostMapping("/admin/check-overdue")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> checkOverdueBooks() {
        ResponseDTO responseDTO = borrowService.checkOverdueBooks();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * 13. Get all active borrows
     * GET /api/borrow/admin/active
     */
    @GetMapping("/admin/active")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getActiveBorrows() {
        ResponseDTO responseDTO = borrowService.getActiveBorrows();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * 14. Get all overdue books
     * GET /api/borrow/admin/overdue
     */
    @GetMapping("/admin/overdue")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getOverdueBooks() {
        ResponseDTO responseDTO = borrowService.getOverdueBooks();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * 15. Get books due for return today
     * GET /api/borrow/admin/today-returns
     */
    @GetMapping("/admin/today-returns")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getTodayReturns() {
        ResponseDTO responseDTO = borrowService.getTodayReturns();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * 16. Get borrowing statistics
     * GET /api/borrow/admin/stats
     */
    @GetMapping("/admin/stats")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getBorrowingStats() {
        ResponseDTO responseDTO = borrowService.getBorrowingStats();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}