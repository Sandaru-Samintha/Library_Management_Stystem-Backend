package com.example.Library_Management_System.repository;

import com.example.Library_Management_System.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    // Search by title OR author (partial & case-insensitive)
    List<Book> findByBookTitleContainingIgnoreCaseOrBookAuthorContainingIgnoreCase(String bookTitle, String bookAuthor);

    // Get only available books
    List<Book> findByBookAvailableTrue();

    // Search by title/author AND only available
    List<Book> findByBookAvailableTrueAndBookTitleContainingIgnoreCaseOrBookAvailableTrueAndBookAuthorContainingIgnoreCase(String bookTitle, String bookAuthor);

    // Check if a book exists by title and author
    boolean existsByBookTitleIgnoreCaseAndBookAuthorIgnoreCase(String bookTitle, String bookAuthor);
}
