package com.example.Library_Management_System.service;


import com.example.Library_Management_System.dto.BookDto;
import com.example.Library_Management_System.entity.Book;
import com.example.Library_Management_System.repository.BookRepository;
import com.example.Library_Management_System.util.VarList;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class BookService {


    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BookRepository bookRepository;

    public  String saveBook(BookDto bookDto){
        if(bookRepository.existsByBookTitleIgnoreCaseAndBookAuthorIgnoreCase(bookDto.getBookTitle(),bookDto.getBookAuthor())){
            return VarList.RSP_DUPLICATED;
        }
        else{
            bookRepository.save(modelMapper.map(bookDto, Book.class));
            return VarList.RSP_SUCCESS;
        }
    }

    public  String updateBook(BookDto bookDto){
        if(bookRepository.existsById(bookDto.getBookID())){
            bookRepository.save(modelMapper.map(bookDto , Book.class));
            return VarList.RSP_SUCCESS;
        }
        else{
            return VarList.RSP_NO_DATA_FOUND;
        }
    }

    public List<BookDto> getAllBooks(){
        List<Book> bookList = bookRepository.findAll();
        return modelMapper.map(bookList, new TypeToken<List<BookDto>>(){}.getType());
    }

    // Search first matching book by title or author
    public BookDto searchBook(String keyword) {
        return bookRepository.findByBookTitleContainingIgnoreCaseOrBookAuthorContainingIgnoreCase(keyword, keyword)
                .stream()
                .findFirst()
                .map(book -> modelMapper.map(book, BookDto.class))
                .orElse(null);
    }

    // Search all matching books
    public List<BookDto> searchBooks(String keyword) {
        return bookRepository.findByBookTitleContainingIgnoreCaseOrBookAuthorContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(book -> modelMapper.map(book, BookDto.class))
                .collect(Collectors.toList());
    }

    // Search only available books
    public List<BookDto> searchAvailableBooks(String keyword) {
        return bookRepository.findByBookAvailableTrueAndBookTitleContainingIgnoreCaseOrBookAvailableTrueAndBookAuthorContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(book -> modelMapper.map(book, BookDto.class))
                .collect(Collectors.toList());
    }

    public  String deleteBook(Long bookID){
        if(bookRepository.existsById(bookID)){
            bookRepository.deleteById(bookID);
            return VarList.RSP_SUCCESS;
        }
        else{
            return VarList.RSP_NO_DATA_FOUND;
        }
    }
}
