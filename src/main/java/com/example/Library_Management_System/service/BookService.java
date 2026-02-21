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

    // ==================== CREATE ====================

    /**
     * Add a new book
     */
    public ResponseDTO addBook(BookDTO bookDTO) {
        try {
            // Check if ISBN already exists
            if (bookDTO.getBookIsbn() != null && !bookDTO.getBookIsbn().isEmpty()
                    && bookRepository.existsByBookIsbn(bookDTO.getBookIsbn())) {
                return new ResponseDTO(VarList.RSP_DUPLICATED, "Book with this ISBN already exists", null, null);
            }

            // Create new book entity
            Book book = new Book();
            book.setBookTitle(bookDTO.getBookTitle());
            book.setBookAuthor(bookDTO.getBookAuthor());
            book.setBookGenre(bookDTO.getBookGenre());
            book.setBookIsbn(bookDTO.getBookIsbn());
            book.setBookPublisher(bookDTO.getBookPublisher());
            book.setBookPublicationYear(bookDTO.getBookPublicationYear());
            book.setBookPrice(bookDTO.getBookPrice());
            book.setBookDescription(bookDTO.getBookDescription());
            book.setShelfLocation(bookDTO.getShelfLocation());
            book.setTotalCopies(bookDTO.getTotalCopies() != null ? bookDTO.getTotalCopies() : 1);
            book.setAvailableCopies(bookDTO.getTotalCopies() != null ? bookDTO.getTotalCopies() : 1);
            book.setBookAvailable(true);

            // Handle book image upload
            if (bookDTO.getBookImage() != null && !bookDTO.getBookImage().isEmpty()) {
                String imageUrl = fileUploadUtil.saveBookImage(bookDTO.getBookImage());
                book.setBookImageUrl(imageUrl);
            }

            // Save book
            Book savedBook = bookRepository.save(book);
            BookDTO savedBookDTO = modelMapper.map(savedBook, BookDTO.class);

            return new ResponseDTO(VarList.RSP_SUCCESS, "Book added successfully", savedBookDTO, null);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to add book: " + e.getMessage(), null, null);
        }
    }

    // ==================== READ ====================

    /**
     * Get all books
     */
    public ResponseDTO getAllBooks() {
        try {
            List<Book> books = bookRepository.findAll();
            List<BookDTO> bookDTOs = modelMapper.map(books, new TypeToken<List<BookDTO>>() {}.getType());
            return new ResponseDTO(VarList.RSP_SUCCESS, "Books retrieved successfully", bookDTOs, null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve books: " + e.getMessage(), null, null);
        }
    }

    /**
     * Get book by ID
     */
    public ResponseDTO getBookById(Long bookId) {
        try {
            Optional<Book> bookOpt = bookRepository.findById(bookId);
            if (bookOpt.isPresent()) {
                BookDTO bookDTO = modelMapper.map(bookOpt.get(), BookDTO.class);
                return new ResponseDTO(VarList.RSP_SUCCESS, "Book found", bookDTO, null);
            } else {
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND, "Book not found with ID: " + bookId, null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve book: " + e.getMessage(), null, null);
        }
    }

    /**
     * Search books by keyword (title or author)
     */
    public ResponseDTO searchBooks(String keyword) {
        try {
            List<Book> books = bookRepository.searchBooks(keyword);
            List<BookDTO> bookDTOs = modelMapper.map(books, new TypeToken<List<BookDTO>>() {}.getType());
            return new ResponseDTO(VarList.RSP_SUCCESS, "Books retrieved successfully", bookDTOs, null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to search books: " + e.getMessage(), null, null);
        }
    }

    /**
     * Get available books
     */
    public ResponseDTO getAvailableBooks() {
        try {
            List<Book> books = bookRepository.findByBookAvailableTrue();
            List<BookDTO> bookDTOs = modelMapper.map(books, new TypeToken<List<BookDTO>>() {}.getType());
            return new ResponseDTO(VarList.RSP_SUCCESS, "Available books retrieved successfully", bookDTOs, null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve available books: " + e.getMessage(), null, null);
        }
    }

    /**
     * Get books by genre
     */
    public ResponseDTO getBooksByGenre(String genre) {
        try {
            List<Book> books = bookRepository.findByBookGenreIgnoreCase(genre);
            List<BookDTO> bookDTOs = modelMapper.map(books, new TypeToken<List<BookDTO>>() {}.getType());
            return new ResponseDTO(VarList.RSP_SUCCESS, "Books retrieved successfully", bookDTOs, null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve books by genre: " + e.getMessage(), null, null);
        }
    }

    /**
     * Get books by author
     */
    public ResponseDTO getBooksByAuthor(String author) {
        try {
            List<Book> books = bookRepository.findByBookAuthorContainingIgnoreCase(author);
            List<BookDTO> bookDTOs = modelMapper.map(books, new TypeToken<List<BookDTO>>() {}.getType());
            return new ResponseDTO(VarList.RSP_SUCCESS, "Books retrieved successfully", bookDTOs, null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve books by author: " + e.getMessage(), null, null);
        }
    }

    // ==================== UPDATE ====================

    /**
     * Update an existing book
     */
    public ResponseDTO updateBook(BookDTO bookDTO) {
        try {
            // Validate bookId
            if (bookDTO.getBookId() == null) {
                return new ResponseDTO(VarList.RSP_ERROR, "Book ID is required for update", null, null);
            }

            // Find existing book
            Optional<Book> existingBookOpt = bookRepository.findById(bookDTO.getBookId());
            if (!existingBookOpt.isPresent()) {
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND, "Book not found with ID: " + bookDTO.getBookId(), null, null);
            }

            Book existingBook = existingBookOpt.get();

            // Update fields only if provided
            if (bookDTO.getBookTitle() != null && !bookDTO.getBookTitle().isEmpty()) {
                existingBook.setBookTitle(bookDTO.getBookTitle());
            }

            if (bookDTO.getBookAuthor() != null && !bookDTO.getBookAuthor().isEmpty()) {
                existingBook.setBookAuthor(bookDTO.getBookAuthor());
            }

            if (bookDTO.getBookGenre() != null && !bookDTO.getBookGenre().isEmpty()) {
                existingBook.setBookGenre(bookDTO.getBookGenre());
            }

            if (bookDTO.getBookIsbn() != null && !bookDTO.getBookIsbn().isEmpty()) {
                // Check if new ISBN is already used by another book
                if (!bookDTO.getBookIsbn().equals(existingBook.getBookIsbn()) &&
                        bookRepository.existsByBookIsbn(bookDTO.getBookIsbn())) {
                    return new ResponseDTO(VarList.RSP_DUPLICATED, "ISBN already exists for another book", null, null);
                }
                existingBook.setBookIsbn(bookDTO.getBookIsbn());
            }

            if (bookDTO.getBookPublisher() != null && !bookDTO.getBookPublisher().isEmpty()) {
                existingBook.setBookPublisher(bookDTO.getBookPublisher());
            }

            if (bookDTO.getBookPublicationYear() != null) {
                existingBook.setBookPublicationYear(bookDTO.getBookPublicationYear());
            }

            if (bookDTO.getBookPrice() != null) {
                existingBook.setBookPrice(bookDTO.getBookPrice());
            }

            if (bookDTO.getBookDescription() != null && !bookDTO.getBookDescription().isEmpty()) {
                existingBook.setBookDescription(bookDTO.getBookDescription());
            }

            if (bookDTO.getShelfLocation() != null && !bookDTO.getShelfLocation().isEmpty()) {
                existingBook.setShelfLocation(bookDTO.getShelfLocation());
            }

            if (bookDTO.getTotalCopies() != null) {
                int currentBorrowed = existingBook.getTotalCopies() - existingBook.getAvailableCopies();
                existingBook.setTotalCopies(bookDTO.getTotalCopies());
                existingBook.setAvailableCopies(bookDTO.getTotalCopies() - currentBorrowed);
                if (existingBook.getAvailableCopies() < 0) {
                    existingBook.setAvailableCopies(0);
                }
            }

            if (bookDTO.getBookAvailable() != null) {
                existingBook.setBookAvailable(bookDTO.getBookAvailable());
            } else {
                // Auto-set availability based on available copies
                existingBook.setBookAvailable(existingBook.getAvailableCopies() > 0);
            }

            // Handle book image upload
            if (bookDTO.getBookImage() != null && !bookDTO.getBookImage().isEmpty()) {
                // Delete old image if exists
                if (existingBook.getBookImageUrl() != null && !existingBook.getBookImageUrl().isEmpty()) {
                    try {
                        fileUploadUtil.deleteImage(existingBook.getBookImageUrl());
                    } catch (Exception e) {
                        System.err.println("Failed to delete old image: " + e.getMessage());
                    }
                }
                String imageUrl = fileUploadUtil.saveBookImage(bookDTO.getBookImage());
                existingBook.setBookImageUrl(imageUrl);
            }

            // Save updated book
            Book updatedBook = bookRepository.save(existingBook);
            BookDTO updatedBookDTO = modelMapper.map(updatedBook, BookDTO.class);

            return new ResponseDTO(VarList.RSP_SUCCESS, "Book updated successfully", updatedBookDTO, null);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to update book: " + e.getMessage(), null, null);
        }
    }

    /**
     * Update book availability (total copies)
     */
    public ResponseDTO updateBookAvailability(Long bookId, Integer totalCopies) {
        try {
            // Log the request for debugging
            System.out.println("Updating book ID: " + bookId + " with total copies: " + totalCopies);

            // Validate input
            if (bookId == null) {
                return new ResponseDTO(VarList.RSP_ERROR, "Book ID is required", null, null);
            }

            if (totalCopies == null || totalCopies < 0) {
                return new ResponseDTO(VarList.RSP_ERROR, "Total copies must be a positive number", null, null);
            }

            // Find the book
            Optional<Book> bookOpt = bookRepository.findById(bookId);
            if (!bookOpt.isPresent()) {
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND, "Book not found with ID: " + bookId, null, null);
            }

            Book book = bookOpt.get();

            // Calculate currently borrowed copies
            int currentlyBorrowed = book.getTotalCopies() - book.getAvailableCopies();

            // Check if new total copies is less than currently borrowed
            if (totalCopies < currentlyBorrowed) {
                return new ResponseDTO(VarList.RSP_ERROR,
                        "Cannot reduce copies below currently borrowed count (" + currentlyBorrowed + ")",
                        null, null);
            }

            // Update the book
            book.setTotalCopies(totalCopies);
            book.setAvailableCopies(totalCopies - currentlyBorrowed);
            book.setBookAvailable(book.getAvailableCopies() > 0);

            Book updatedBook = bookRepository.save(book);
            BookDTO updatedBookDTO = modelMapper.map(updatedBook, BookDTO.class);

            return new ResponseDTO(VarList.RSP_SUCCESS,
                    "Book availability updated successfully. Available copies: " + book.getAvailableCopies(),
                    updatedBookDTO, null);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to update book availability: " + e.getMessage(), null, null);
        }
    }

    // ==================== DELETE ====================

    /**
     * Delete a book by ID
     */
    public ResponseDTO deleteBook(Long bookId) {
        try {
            // Check if book exists
            Optional<Book> bookOpt = bookRepository.findById(bookId);
            if (!bookOpt.isPresent()) {
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND, "Book not found with ID: " + bookId, null, null);
            }

            // Delete book image if exists
            Book book = bookOpt.get();
            if (book.getBookImageUrl() != null && !book.getBookImageUrl().isEmpty()) {
                try {
                    fileUploadUtil.deleteImage(book.getBookImageUrl());
                } catch (Exception e) {
                    System.err.println("Failed to delete image: " + e.getMessage());
                }
            }

            // Delete the book
            bookRepository.deleteById(bookId);
            return new ResponseDTO(VarList.RSP_SUCCESS, "Book deleted successfully", null, null);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to delete book: " + e.getMessage(), null, null);
        }
    }

    // ==================== BULK OPERATIONS ====================

    /**
     * Delete multiple books
     */
    public ResponseDTO deleteMultipleBooks(List<Long> bookIds) {
        try {
            int successCount = 0;
            int failCount = 0;

            for (Long bookId : bookIds) {
                try {
                    Optional<Book> bookOpt = bookRepository.findById(bookId);
                    if (bookOpt.isPresent()) {
                        // Delete image if exists
                        if (bookOpt.get().getBookImageUrl() != null) {
                            fileUploadUtil.deleteImage(bookOpt.get().getBookImageUrl());
                        }
                        bookRepository.deleteById(bookId);
                        successCount++;
                    } else {
                        failCount++;
                    }
                } catch (Exception e) {
                    failCount++;
                }
            }

            String message = "Deleted " + successCount + " books";
            if (failCount > 0) {
                message += ". Failed to delete " + failCount + " books";
            }

            return new ResponseDTO(VarList.RSP_SUCCESS, message, null, null);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to delete books: " + e.getMessage(), null, null);
        }
    }
}