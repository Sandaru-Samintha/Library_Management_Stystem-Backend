package com.example.Library_Management_System.repository;

import com.example.Library_Management_System.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book,Long> {

}
