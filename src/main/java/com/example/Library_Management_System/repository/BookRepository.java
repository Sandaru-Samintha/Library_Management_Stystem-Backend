package com.example.Library_Management_System.repository;

import com.example.Library_Management_System.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByBookTitleContainingIgnoreCase(String title);

    List<Book> findByBookAuthorContainingIgnoreCase(String author);

    @Query("SELECT b FROM Book b WHERE LOWER(b.bookTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.bookAuthor) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Book> searchBooks(@Param("keyword") String keyword);

    List<Book> findByBookAvailableTrue();

    List<Book> findByBookGenreIgnoreCase(String genre);

    boolean existsByBookIsbn(String isbn);

    Book findByBookIsbn(String isbn);
}