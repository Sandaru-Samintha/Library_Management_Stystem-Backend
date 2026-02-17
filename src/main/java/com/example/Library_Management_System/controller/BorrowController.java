package com.example.Library_Management_System.controller;

        import com.example.Library_Management_System.dto.ResponseDTO;
        import com.example.Library_Management_System.service.BorrowService;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.ResponseEntity;
        import org.springframework.security.access.prepost.PreAuthorize;
        import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrow")
@CrossOrigin
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    @PostMapping("/book/{bookId}")
    @PreAuthorize("hasAuthority('MEMBER')")
    public ResponseEntity<ResponseDTO> borrowBook(@PathVariable Long bookId) {
        ResponseDTO responseDTO = borrowService.borrowBook(bookId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PutMapping("/return/{borrowId}")
    @PreAuthorize("hasAuthority('MEMBER')")
    public ResponseEntity<ResponseDTO> returnBook(@PathVariable Long borrowId) {
        ResponseDTO responseDTO = borrowService.returnBook(borrowId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/my-books")
    @PreAuthorize("hasAuthority('MEMBER')")
    public ResponseEntity<ResponseDTO> getMyBorrowedBooks() {
        ResponseDTO responseDTO = borrowService.getMyBorrowedBooks();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getAllBorrowedBooks() {
        ResponseDTO responseDTO = borrowService.getAllBorrowedBooks();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping("/admin/check-overdue")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> checkOverdueBooks() {
        ResponseDTO responseDTO = borrowService.checkOverdueBooks();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}