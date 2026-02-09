package com.example.Library_Management_System.controller;

import com.example.Library_Management_System.dto.BookDto;
import com.example.Library_Management_System.dto.ResponseDto;
import com.example.Library_Management_System.service.BookService;
import com.example.Library_Management_System.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/book")
@CrossOrigin
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private ResponseDto responseDto;

    @PostMapping("/saveBook")
    public ResponseEntity saveBook(@RequestBody BookDto bookDto){
        try {
            String res = bookService.saveBook(bookDto);
            if(res.equals("00")){
                responseDto.setCode(VarList.RSP_SUCCESS);
                responseDto.setMessage("Success");
                responseDto.setContent(bookDto);

                return new ResponseEntity(responseDto, HttpStatus.ACCEPTED);
            } else if (res.equals("06")){
                responseDto.setCode(VarList.RSP_DUPLICATED);
                responseDto.setMessage("Book is already exist");
                responseDto.setContent(bookDto);

                return new ResponseEntity(responseDto,HttpStatus.BAD_REQUEST);
            }else{
                responseDto.setCode(VarList.RSP_FAIL);
                responseDto.setMessage("Error");
                responseDto.setContent(null);

                return new ResponseEntity(responseDto,HttpStatus.BAD_REQUEST);
            }

        }catch (Exception ex){
            responseDto.setCode(VarList.RSP_FAIL);
            responseDto.setMessage(ex.getMessage());
            responseDto.setContent(null);

            return new ResponseEntity(responseDto,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updateBook")
    public ResponseEntity updateBook(@RequestBody BookDto bookDto){
        try{
            String res = bookService.updateBook(bookDto);
            if(res.equals("00")){
                responseDto.setCode(VarList.RSP_SUCCESS);
                responseDto.setMessage("Update The Book");
                responseDto.setContent(bookDto);

                return new ResponseEntity(responseDto,HttpStatus.ACCEPTED);
            } else if (res.equals("01")) {
                responseDto.setCode(VarList.RSP_NO_DATA_FOUND);
                responseDto.setMessage("Not A Existed Book");
                responseDto.setContent(bookDto);

                return new ResponseEntity(responseDto,HttpStatus.BAD_REQUEST);
            }else{
                responseDto.setCode(VarList.RSP_FAIL);
                responseDto.setMessage("Error");
                responseDto.setContent(null);

                return new ResponseEntity(responseDto,HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex){
            responseDto.setCode(VarList.RSP_FAIL);
            responseDto.setMessage(ex.getMessage());
            responseDto.setContent(null);
            return new ResponseEntity(responseDto,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/getAllBooks")
    public ResponseEntity getAllBooks(){
        try{
            List<BookDto> bookDtoList = bookService.getAllBooks();
            responseDto.setCode(VarList.RSP_SUCCESS);
            responseDto.setMessage("Success");
            responseDto.setContent(bookDtoList);

            return new ResponseEntity(responseDto,HttpStatus.ACCEPTED);
        }catch (Exception ex){
            responseDto.setCode(VarList.RSP_FAIL);
            responseDto.setMessage(ex.getMessage());
            responseDto.setContent(null);
            return new ResponseEntity(responseDto,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // Search first matching book by title or author
    @GetMapping("/searchBook/{keyword}")
    public ResponseEntity<ResponseDto> searchBook(@PathVariable String keyword) {
        try {
            BookDto bookDto = bookService.searchBook(keyword);

            if (bookDto != null) {
                responseDto.setCode(VarList.RSP_SUCCESS);
                responseDto.setMessage("Book found");
                responseDto.setContent(bookDto);
                return new ResponseEntity<>(responseDto, HttpStatus.OK);
            } else {
                responseDto.setCode(VarList.RSP_NO_DATA_FOUND);
                responseDto.setMessage("No matching book found");
                responseDto.setContent(null);
                return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            responseDto.setCode(VarList.RSP_FAIL);
            responseDto.setMessage(ex.getMessage());
            responseDto.setContent(null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Search all matching books
    @GetMapping("/searchBooks")
    public ResponseEntity<ResponseDto> searchBooks(@RequestParam String keyword) {
        try {
            List<BookDto> books = bookService.searchBooks(keyword);

            if (!books.isEmpty()) {
                responseDto.setCode(VarList.RSP_SUCCESS);
                responseDto.setMessage("Books found");
                responseDto.setContent(books);
                return new ResponseEntity<>(responseDto, HttpStatus.OK);
            } else {
                responseDto.setCode(VarList.RSP_NO_DATA_FOUND);
                responseDto.setMessage("No books found");
                responseDto.setContent(null);
                return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            responseDto.setCode(VarList.RSP_FAIL);
            responseDto.setMessage(ex.getMessage());
            responseDto.setContent(null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Search only available books
    @GetMapping("/searchAvailableBooks")
    public ResponseEntity<ResponseDto> searchAvailableBooks(@RequestParam String keyword) {
        try {
            List<BookDto> books = bookService.searchAvailableBooks(keyword);

            if (!books.isEmpty()) {
                responseDto.setCode(VarList.RSP_SUCCESS);
                responseDto.setMessage("Available books found");
                responseDto.setContent(books);
                return new ResponseEntity<>(responseDto, HttpStatus.OK);
            } else {
                responseDto.setCode(VarList.RSP_NO_DATA_FOUND);
                responseDto.setMessage("No available books found");
                responseDto.setContent(null);
                return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            responseDto.setCode(VarList.RSP_FAIL);
            responseDto.setMessage(ex.getMessage());
            responseDto.setContent(null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteBook/{bookID}")
    public ResponseEntity deleteBook(@PathVariable long bookID){
        try{
            String res = bookService.deleteBook(bookID);
            if(res.equals("00")){
                responseDto.setCode(VarList.RSP_SUCCESS);
                responseDto.setMessage("Delete The Book");
                responseDto.setContent(bookID);
                return new ResponseEntity(responseDto,HttpStatus.ACCEPTED);
            }
            else {
                responseDto.setCode(VarList.RSP_NO_DATA_FOUND);
                responseDto.setMessage("No  Existed Book Available In The bookID");
                responseDto.setContent(null);
                return new ResponseEntity(responseDto,HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex){
            responseDto.setCode(VarList.RSP_FAIL);
            responseDto.setMessage(ex.getMessage());
            responseDto.setContent(null);
            return new ResponseEntity(responseDto,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




}
