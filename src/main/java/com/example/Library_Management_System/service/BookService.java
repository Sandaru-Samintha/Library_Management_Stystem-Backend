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
        List<BookDto> bookList = getAllBooks();
        return modelMapper.map(bookList, new TypeToken<List<BookDto>>(){}.getType());
    }

    public BookDto searchBook(Long bookID){
        if (bookRepository.existsById(bookID)) {
            Book book = bookRepository.findById(bookID).orElse(null);
            return modelMapper.map(book, BookDto.class);
        }
        else {
            return null;
        }
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
