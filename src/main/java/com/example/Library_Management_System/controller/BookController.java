package com.example.Library_Management_System.controller;

import com.example.Library_Management_System.dto.BookDTO;
import com.example.Library_Management_System.dto.ResponseDTO;
import com.example.Library_Management_System.service.BookService;
import com.example.Library_Management_System.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BookController {

    @Autowired
    private BookService bookService;

    // ==================== PUBLIC ENDPOINTS (No Authentication Required) ====================

    /**
     * Search books by keyword (title or author)
     */
    @GetMapping("/public/search")
    public ResponseEntity<ResponseDTO> searchBooks(@RequestParam String keyword) {
        ResponseDTO responseDTO = bookService.searchBooks(keyword);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * Get all available books
     */
    @GetMapping("/public/available")
    public ResponseEntity<ResponseDTO> getAvailableBooks() {
        ResponseDTO responseDTO = bookService.getAvailableBooks();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * Get book by ID
     */
    @GetMapping("/public/{bookId}")
    public ResponseEntity<ResponseDTO> getBookById(@PathVariable Long bookId) {
        ResponseDTO responseDTO = bookService.getBookById(bookId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * Get all books
     */
    @GetMapping("/public/all")
    public ResponseEntity<ResponseDTO> getAllBooks() {
        ResponseDTO responseDTO = bookService.getAllBooks();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    // ==================== ADMIN ENDPOINTS (Requires ADMIN Role) ====================

    /**
     * Add a new book
     */
    @PostMapping("/admin/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> addBook(@ModelAttribute BookDTO bookDTO) {
        ResponseDTO responseDTO = bookService.addBook(bookDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * Update an existing book
     */
    @PutMapping("/admin/update")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> updateBook(@ModelAttribute BookDTO bookDTO) {
        ResponseDTO responseDTO = bookService.updateBook(bookDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * Delete a book by ID
     */
    @DeleteMapping("/admin/delete/{bookId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> deleteBook(@PathVariable Long bookId) {
        ResponseDTO responseDTO = bookService.deleteBook(bookId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * Update book availability (total copies)
     * Example: /api/books/admin/1/availability?totalCopies=10
     */
    @PutMapping("/admin/{bookId}/availability")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> updateBookAvailability(
            @PathVariable Long bookId,
            @RequestParam Integer totalCopies) {
        ResponseDTO responseDTO = bookService.updateBookAvailability(bookId, totalCopies);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * Get books by genre
     */
    @GetMapping("/public/genre/{genre}")
    public ResponseEntity<ResponseDTO> getBooksByGenre(@PathVariable String genre) {
        // You would need to add this method to service
        ResponseDTO responseDTO = bookService.getBooksByGenre(genre);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * Get books by author
     */
    @GetMapping("/public/author/{author}")
    public ResponseEntity<ResponseDTO> getBooksByAuthor(@PathVariable String author) {
        // You would need to add this method to service
        ResponseDTO responseDTO = bookService.getBooksByAuthor(author);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}