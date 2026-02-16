package com.example.Library_Management_System.service;

import com.example.Library_Management_System.dto.BookDTO;
import com.example.Library_Management_System.dto.ResponseDTO;
import com.example.Library_Management_System.entity.Book;
import com.example.Library_Management_System.repository.BookRepository;
import com.example.Library_Management_System.util.VarList;
import com.example.Library_Management_System.util.FileUploadUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @Autowired
    private ModelMapper modelMapper;

    public ResponseDTO addBook(BookDTO bookDTO) {
        try {
            if (bookDTO.getBookIsbn() != null && bookRepository.existsByBookIsbn(bookDTO.getBookIsbn())) {
                return new ResponseDTO(VarList.RSP_DUPLICATED, "Book with this ISBN already exists", null, null);
            }

            // Convert DTO to Entity using ModelMapper
            Book book = modelMapper.map(bookDTO, Book.class);

            // Set additional fields not in DTO
            book.setTotalCopies(bookDTO.getTotalCopies() != null ? bookDTO.getTotalCopies() : 1);
            book.setAvailableCopies(bookDTO.getTotalCopies() != null ? bookDTO.getTotalCopies() : 1);
            book.setBookAvailable(true);

            // Handle book image upload
            if (bookDTO.getBookImage() != null && !bookDTO.getBookImage().isEmpty()) {
                String imageUrl = fileUploadUtil.saveBookImage(bookDTO.getBookImage());
                book.setBookImageUrl(imageUrl);
            }

            Book savedBook = bookRepository.save(book);

            // Convert back to DTO for response
            BookDTO savedBookDTO = modelMapper.map(savedBook, BookDTO.class);
            return new ResponseDTO(VarList.RSP_SUCCESS, "Book added successfully", savedBookDTO, null);

        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to add book: " + e.getMessage(), null, null);
        }
    }

    public ResponseDTO updateBook(BookDTO bookDTO) {
        try {
            Optional<Book> existingBookOpt = bookRepository.findById(bookDTO.getBookId());
            if (!existingBookOpt.isPresent()) {
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND, "Book not found", null, null);
            }

            Book existingBook = existingBookOpt.get();

            // Update existing book with DTO values using ModelMapper
            modelMapper.map(bookDTO, existingBook);

            // Handle book image upload
            if (bookDTO.getBookImage() != null && !bookDTO.getBookImage().isEmpty()) {
                String imageUrl = fileUploadUtil.saveBookImage(bookDTO.getBookImage());
                existingBook.setBookImageUrl(imageUrl);
            }

            Book updatedBook = bookRepository.save(existingBook);

            // Convert to DTO for response
            BookDTO updatedBookDTO = modelMapper.map(updatedBook, BookDTO.class);
            return new ResponseDTO(VarList.RSP_SUCCESS, "Book updated successfully", updatedBookDTO, null);

        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to update book: " + e.getMessage(), null, null);
        }
    }

    public ResponseDTO deleteBook(Long bookId) {
        try {
            if (!bookRepository.existsById(bookId)) {
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND, "Book not found", null, null);
            }
            bookRepository.deleteById(bookId);
            return new ResponseDTO(VarList.RSP_SUCCESS, "Book deleted successfully", null, null);
        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to delete book: " + e.getMessage(), null, null);
        }
    }

    public ResponseDTO getAllBooks() {
        try {
            List<Book> books = bookRepository.findAll();

            // Convert List<Book> to List<BookDTO> using ModelMapper
            List<BookDTO> bookDTOs = modelMapper.map(books, new TypeToken<List<BookDTO>>() {}.getType());
            return new ResponseDTO(VarList.RSP_SUCCESS, "Books retrieved successfully", bookDTOs, null);
        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve books: " + e.getMessage(), null, null);
        }
    }

    public ResponseDTO getBookById(Long bookId) {
        try {
            Optional<Book> bookOpt = bookRepository.findById(bookId);
            if (bookOpt.isPresent()) {
                // Convert Entity to DTO using ModelMapper
                BookDTO bookDTO = modelMapper.map(bookOpt.get(), BookDTO.class);
                return new ResponseDTO(VarList.RSP_SUCCESS, "Book found", bookDTO, null);
            } else {
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND, "Book not found", null, null);
            }
        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve book: " + e.getMessage(), null, null);
        }
    }

    public ResponseDTO searchBooks(String keyword) {
        try {
            List<Book> books = bookRepository.searchBooks(keyword);

            // Convert List<Book> to List<BookDTO> using ModelMapper
            List<BookDTO> bookDTOs = modelMapper.map(books, new TypeToken<List<BookDTO>>() {}.getType());
            return new ResponseDTO(VarList.RSP_SUCCESS, "Books retrieved successfully", bookDTOs, null);
        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to search books: " + e.getMessage(), null, null);
        }
    }

    public ResponseDTO getAvailableBooks() {
        try {
            List<Book> books = bookRepository.findByBookAvailableTrue();

            // Convert List<Book> to List<BookDTO> using ModelMapper
            List<BookDTO> bookDTOs = modelMapper.map(books, new TypeToken<List<BookDTO>>() {}.getType());
            return new ResponseDTO(VarList.RSP_SUCCESS, "Available books retrieved successfully", bookDTOs, null);
        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve available books: " + e.getMessage(), null, null);
        }
    }
}