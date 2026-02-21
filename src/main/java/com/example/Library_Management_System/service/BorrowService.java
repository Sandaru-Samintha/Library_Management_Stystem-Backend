package com.example.Library_Management_System.service;

import com.example.Library_Management_System.dto.BorrowRecordDTO;
import com.example.Library_Management_System.dto.ResponseDTO;
import com.example.Library_Management_System.entity.*;
import com.example.Library_Management_System.repository.BookRepository;
import com.example.Library_Management_System.repository.BorrowRecordRepository;
import com.example.Library_Management_System.repository.MemberRepository;
import com.example.Library_Management_System.repository.FineRepository;
import com.example.Library_Management_System.util.VarList;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BorrowService {

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private FineService fineService;

    @Autowired
    private ModelMapper modelMapper;

    // ==================== MEMBER METHODS ====================

    /**
     * 1. Borrow a book
     */
    public ResponseDTO borrowBook(Long bookId) {
        try {
            String email = getCurrentUserEmail();
            Optional<Member> memberOpt = memberRepository.findByMemEmail(email);

            if (!memberOpt.isPresent()) {
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND, "Member not found", null, null);
            }

            Member member = memberOpt.get();

            // Check if member is active
            if (!member.isActive()) {
                return new ResponseDTO(VarList.RSP_ERROR, "Your account is deactivated. Please contact admin.", null, null);
            }

            Optional<Book> bookOpt = bookRepository.findById(bookId);
            if (!bookOpt.isPresent()) {
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND, "Book not found", null, null);
            }

            Book book = bookOpt.get();

            // Check if book is available
            if (!book.getBookAvailable() || book.getAvailableCopies() <= 0) {
                return new ResponseDTO(VarList.RSP_ERROR, "Book is not available for borrowing", null, null);
            }

            // Check if member already borrowed this book
            if (borrowRecordRepository.existsByMemberAndBookAndStatus(member, book, BorrowStatus.BORROWED)) {
                return new ResponseDTO(VarList.RSP_ERROR, "You have already borrowed this book", null, null);
            }

            // Check if member has overdue books
            List<BorrowRecord> overdueBooks = borrowRecordRepository
                    .findByMemberAndStatus(member, BorrowStatus.OVERDUE);
            if (!overdueBooks.isEmpty()) {
                return new ResponseDTO(VarList.RSP_ERROR,
                        "You have overdue books. Please return them before borrowing new books.", null, null);
            }

            // Create borrow record
            BorrowRecord borrowRecord = new BorrowRecord();
            borrowRecord.setMember(member);
            borrowRecord.setBook(book);
            borrowRecord.setBorrowDate(LocalDate.now());
            borrowRecord.setDueDate(LocalDate.now().plusDays(14)); // 14 days borrowing period
            borrowRecord.setStatus(BorrowStatus.BORROWED);

            // Update book availability
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            if (book.getAvailableCopies() <= 0) {
                book.setBookAvailable(false);
            }
            bookRepository.save(book);

            BorrowRecord savedRecord = borrowRecordRepository.save(borrowRecord);
            BorrowRecordDTO savedRecordDTO = modelMapper.map(savedRecord, BorrowRecordDTO.class);

            return new ResponseDTO(VarList.RSP_SUCCESS,
                    "Book borrowed successfully. Due date: " + savedRecord.getDueDate(),
                    savedRecordDTO, null);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to borrow book: " + e.getMessage(), null, null);
        }
    }

    /**
     * 2. Return a borrowed book
     */
    public ResponseDTO returnBook(Long borrowId) {
        try {
            Optional<BorrowRecord> borrowRecordOpt = borrowRecordRepository.findById(borrowId);

            if (!borrowRecordOpt.isPresent()) {
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND, "Borrow record not found", null, null);
            }

            BorrowRecord borrowRecord = borrowRecordOpt.get();

            // Verify that the book belongs to the current member
            String email = getCurrentUserEmail();
            if (!borrowRecord.getMember().getMemEmail().equals(email)) {
                return new ResponseDTO(VarList.RSP_ERROR, "You can only return books you borrowed", null, null);
            }

            if (borrowRecord.getStatus() == BorrowStatus.RETURNED) {
                return new ResponseDTO(VarList.RSP_ERROR, "Book already returned", null, null);
            }

            LocalDate returnDate = LocalDate.now();
            borrowRecord.setReturnDate(returnDate);
            borrowRecord.setStatus(BorrowStatus.RETURNED);

            // Calculate fine if overdue
            double fineAmount = 0;
            if (returnDate.isAfter(borrowRecord.getDueDate())) {
                long daysOverdue = ChronoUnit.DAYS.between(borrowRecord.getDueDate(), returnDate);
                fineAmount = daysOverdue * 10.0; // Rs. 10 per day
                borrowRecord.setFineAmount(fineAmount);

                // Create fine record
                fineService.createFine(borrowRecord);
            }

            // Update book availability
            Book book = borrowRecord.getBook();
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            book.setBookAvailable(true);
            bookRepository.save(book);

            BorrowRecord updatedRecord = borrowRecordRepository.save(borrowRecord);
            BorrowRecordDTO updatedRecordDTO = modelMapper.map(updatedRecord, BorrowRecordDTO.class);

            String message = "Book returned successfully";
            if (fineAmount > 0) {
                message += String.format(" with a fine of Rs. %.2f", fineAmount);
            }

            return new ResponseDTO(VarList.RSP_SUCCESS, message, updatedRecordDTO, null);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to return book: " + e.getMessage(), null, null);
        }
    }

    /**
     * 3. Get my borrowed books
     */
    public ResponseDTO getMyBorrowedBooks() {
        try {
            String email = getCurrentUserEmail();
            Optional<Member> memberOpt = memberRepository.findByMemEmail(email);

            if (!memberOpt.isPresent()) {
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND, "Member not found", null, null);
            }

            List<BorrowRecord> borrowRecords = borrowRecordRepository
                    .findByMemberAndStatus(memberOpt.get(), BorrowStatus.BORROWED);

            List<BorrowRecordDTO> borrowDTOs = modelMapper.map(borrowRecords,
                    new TypeToken<List<BorrowRecordDTO>>() {}.getType());

            return new ResponseDTO(VarList.RSP_SUCCESS, "Borrowed books retrieved successfully", borrowDTOs, null);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve borrowed books: " + e.getMessage(), null, null);
        }
    }

    /**
     * 4. Get my complete borrow history
     */
    public ResponseDTO getMyBorrowHistory() {
        try {
            String email = getCurrentUserEmail();
            Optional<Member> memberOpt = memberRepository.findByMemEmail(email);

            if (!memberOpt.isPresent()) {
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND, "Member not found", null, null);
            }

            List<BorrowRecord> borrowRecords = borrowRecordRepository.findByMember(memberOpt.get());

            List<BorrowRecordDTO> borrowDTOs = modelMapper.map(borrowRecords,
                    new TypeToken<List<BorrowRecordDTO>>() {}.getType());

            return new ResponseDTO(VarList.RSP_SUCCESS, "Borrow history retrieved successfully", borrowDTOs, null);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve borrow history: " + e.getMessage(), null, null);
        }
    }

    /**
     * 5. Get current borrowed books
     */
    public ResponseDTO getCurrentBorrows() {
        return getMyBorrowedBooks(); // Same as my borrowed books
    }

    /**
     * 6. Check if member has overdue books
     */
    public ResponseDTO checkMyOverdueBooks() {
        try {
            String email = getCurrentUserEmail();
            Optional<Member> memberOpt = memberRepository.findByMemEmail(email);

            if (!memberOpt.isPresent()) {
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND, "Member not found", null, null);
            }

            List<BorrowRecord> overdueRecords = borrowRecordRepository
                    .findByMemberAndStatus(memberOpt.get(), BorrowStatus.OVERDUE);

            List<BorrowRecordDTO> overdueDTOs = modelMapper.map(overdueRecords,
                    new TypeToken<List<BorrowRecordDTO>>() {}.getType());

            String message = overdueRecords.isEmpty() ?
                    "No overdue books" : "You have " + overdueRecords.size() + " overdue book(s)";

            return new ResponseDTO(VarList.RSP_SUCCESS, message, overdueDTOs, null);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to check overdue books: " + e.getMessage(), null, null);
        }
    }

    // ==================== ADMIN METHODS ====================

    /**
     * 7. Get all borrow records
     */
    public ResponseDTO getAllBorrowRecords() {
        try {
            List<BorrowRecord> records = borrowRecordRepository.findAll();
            List<BorrowRecordDTO> recordDTOs = modelMapper.map(records,
                    new TypeToken<List<BorrowRecordDTO>>() {}.getType());
            return new ResponseDTO(VarList.RSP_SUCCESS, "All borrow records retrieved successfully", recordDTOs, null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve borrow records: " + e.getMessage(), null, null);
        }
    }

    /**
     * 8. Get borrows by status
     */
    public ResponseDTO getBorrowsByStatus(BorrowStatus status) {
        try {
            List<BorrowRecord> records = borrowRecordRepository.findByStatus(status);
            List<BorrowRecordDTO> recordDTOs = modelMapper.map(records,
                    new TypeToken<List<BorrowRecordDTO>>() {}.getType());
            return new ResponseDTO(VarList.RSP_SUCCESS,
                    status + " records retrieved successfully", recordDTOs, null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve borrow records: " + e.getMessage(), null, null);
        }
    }

    /**
     * 9. Get borrow history for a specific member
     */
    public ResponseDTO getMemberBorrowHistory(Long memberId) {
        try {
            Optional<Member> memberOpt = memberRepository.findById(memberId);
            if (!memberOpt.isPresent()) {
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND, "Member not found", null, null);
            }

            List<BorrowRecord> records = borrowRecordRepository.findByMember(memberOpt.get());
            List<BorrowRecordDTO> recordDTOs = modelMapper.map(records,
                    new TypeToken<List<BorrowRecordDTO>>() {}.getType());

            return new ResponseDTO(VarList.RSP_SUCCESS,
                    "Borrow history for member " + memberOpt.get().getMemFullName(), recordDTOs, null);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve member borrow history: " + e.getMessage(), null, null);
        }
    }

    /**
     * 10. Get borrow history for a specific book
     */
    public ResponseDTO getBookBorrowHistory(Long bookId) {
        try {
            Optional<Book> bookOpt = bookRepository.findById(bookId);
            if (!bookOpt.isPresent()) {
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND, "Book not found", null, null);
            }

            List<BorrowRecord> records = borrowRecordRepository.findByBook(bookOpt.get());
            List<BorrowRecordDTO> recordDTOs = modelMapper.map(records,
                    new TypeToken<List<BorrowRecordDTO>>() {}.getType());

            return new ResponseDTO(VarList.RSP_SUCCESS,
                    "Borrow history for book " + bookOpt.get().getBookTitle(), recordDTOs, null);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve book borrow history: " + e.getMessage(), null, null);
        }
    }

    /**
     * 11. Extend due date
     */
    public ResponseDTO extendDueDate(Long borrowId, int additionalDays) {
        try {
            Optional<BorrowRecord> recordOpt = borrowRecordRepository.findById(borrowId);
            if (!recordOpt.isPresent()) {
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND, "Borrow record not found", null, null);
            }

            BorrowRecord record = recordOpt.get();

            if (record.getStatus() != BorrowStatus.BORROWED) {
                return new ResponseDTO(VarList.RSP_ERROR, "Can only extend due date for active borrows", null, null);
            }

            LocalDate newDueDate = record.getDueDate().plusDays(additionalDays);
            record.setDueDate(newDueDate);

            BorrowRecord updatedRecord = borrowRecordRepository.save(record);
            BorrowRecordDTO updatedDTO = modelMapper.map(updatedRecord, BorrowRecordDTO.class);

            return new ResponseDTO(VarList.RSP_SUCCESS,
                    "Due date extended by " + additionalDays + " days. New due date: " + newDueDate,
                    updatedDTO, null);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to extend due date: " + e.getMessage(), null, null);
        }
    }

    /**
     * 12. Check and update overdue books
     */
    public ResponseDTO checkOverdueBooks() {
        try {
            LocalDate today = LocalDate.now();
            List<BorrowRecord> overdueRecords = borrowRecordRepository
                    .findByDueDateBeforeAndStatus(today, BorrowStatus.BORROWED);

            int updatedCount = 0;
            for (BorrowRecord record : overdueRecords) {
                record.setStatus(BorrowStatus.OVERDUE);
                borrowRecordRepository.save(record);

                // Create fine for overdue books
                if (record.getFineAmount() == null || record.getFineAmount() == 0) {
                    long daysOverdue = ChronoUnit.DAYS.between(record.getDueDate(), today);
                    double fineAmount = daysOverdue * 10.0;
                    record.setFineAmount(fineAmount);
                    fineService.createFine(record);
                }
                updatedCount++;
            }

            List<BorrowRecordDTO> overdueDTOs = modelMapper.map(overdueRecords,
                    new TypeToken<List<BorrowRecordDTO>>() {}.getType());

            return new ResponseDTO(VarList.RSP_SUCCESS,
                    "Overdue books checked. " + updatedCount + " books marked as overdue.",
                    overdueDTOs, null);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to check overdue books: " + e.getMessage(), null, null);
        }
    }

    /**
     * 13. Get active borrows
     */
    public ResponseDTO getActiveBorrows() {
        try {
            List<BorrowRecord> records = borrowRecordRepository.findByStatus(BorrowStatus.BORROWED);
            List<BorrowRecordDTO> recordDTOs = modelMapper.map(records,
                    new TypeToken<List<BorrowRecordDTO>>() {}.getType());
            return new ResponseDTO(VarList.RSP_SUCCESS, "Active borrows retrieved successfully", recordDTOs, null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve active borrows: " + e.getMessage(), null, null);
        }
    }

    /**
     * 14. Get overdue books
     */
    public ResponseDTO getOverdueBooks() {
        try {
            List<BorrowRecord> records = borrowRecordRepository.findByStatus(BorrowStatus.OVERDUE);
            List<BorrowRecordDTO> recordDTOs = modelMapper.map(records,
                    new TypeToken<List<BorrowRecordDTO>>() {}.getType());
            return new ResponseDTO(VarList.RSP_SUCCESS, "Overdue books retrieved successfully", recordDTOs, null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve overdue books: " + e.getMessage(), null, null);
        }
    }

    /**
     * 15. Get books due for return today
     */
    public ResponseDTO getTodayReturns() {
        try {
            LocalDate today = LocalDate.now();
            List<BorrowRecord> records = borrowRecordRepository.findAll().stream()
                    .filter(r -> r.getStatus() == BorrowStatus.BORROWED && r.getDueDate().equals(today))
                    .collect(Collectors.toList());

            List<BorrowRecordDTO> recordDTOs = modelMapper.map(records,
                    new TypeToken<List<BorrowRecordDTO>>() {}.getType());

            return new ResponseDTO(VarList.RSP_SUCCESS,
                    "Books due for return today: " + records.size(), recordDTOs, null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve today's returns: " + e.getMessage(), null, null);
        }
    }

    /**
     * 16. Get borrowing statistics
     */
    public ResponseDTO getBorrowingStats() {
        try {
            Map<String, Object> stats = new HashMap<>();

            long totalBorrows = borrowRecordRepository.count();
            long activeBorrows = borrowRecordRepository.findByStatus(BorrowStatus.BORROWED).size();
            long overdueBorrows = borrowRecordRepository.findByStatus(BorrowStatus.OVERDUE).size();
            long returnedBorrows = borrowRecordRepository.findByStatus(BorrowStatus.RETURNED).size();

            // Today's borrows and returns
            LocalDate today = LocalDate.now();
            long todayBorrows = borrowRecordRepository.findAll().stream()
                    .filter(b -> b.getBorrowDate().equals(today))
                    .count();
            long todayReturns = borrowRecordRepository.findAll().stream()
                    .filter(b -> b.getReturnDate() != null && b.getReturnDate().equals(today))
                    .count();

            stats.put("totalBorrows", totalBorrows);
            stats.put("activeBorrows", activeBorrows);
            stats.put("overdueBorrows", overdueBorrows);
            stats.put("returnedBorrows", returnedBorrows);
            stats.put("todayBorrows", todayBorrows);
            stats.put("todayReturns", todayReturns);

            return new ResponseDTO(VarList.RSP_SUCCESS, "Borrowing statistics retrieved", stats, null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve statistics: " + e.getMessage(), null, null);
        }
    }

    // ==================== UTILITY METHODS ====================

    private String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }
}