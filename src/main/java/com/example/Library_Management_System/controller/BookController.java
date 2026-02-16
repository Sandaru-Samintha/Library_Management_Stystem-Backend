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
@CrossOrigin
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/public/search")
    public ResponseEntity<ResponseDTO> searchBooks(@RequestParam String keyword) {
        ResponseDTO responseDTO = bookService.searchBooks(keyword);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/public/available")
    public ResponseEntity<ResponseDTO> getAvailableBooks() {
        ResponseDTO responseDTO = bookService.getAvailableBooks();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/public/{bookId}")
    public ResponseEntity<ResponseDTO> getBookById(@PathVariable Long bookId) {
        ResponseDTO responseDTO = bookService.getBookById(bookId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/public/all")
    public ResponseEntity<ResponseDTO> getAllBooks() {
        ResponseDTO responseDTO = bookService.getAllBooks();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping("/admin/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> addBook(@ModelAttribute BookDTO bookDTO) {
        ResponseDTO responseDTO = bookService.addBook(bookDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PutMapping("/admin/update")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> updateBook(@ModelAttribute BookDTO bookDTO) {
        ResponseDTO responseDTO = bookService.updateBook(bookDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/admin/delete/{bookId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> deleteBook(@PathVariable Long bookId) {
        ResponseDTO responseDTO = bookService.deleteBook(bookId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}