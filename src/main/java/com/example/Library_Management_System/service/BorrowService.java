package com.example.Library_Management_System.service;


import com.example.Library_Management_System.dto.BorrowRecordDTO;
import com.example.Library_Management_System.dto.ResponseDTO;
import com.example.Library_Management_System.entity.Book;
import com.example.Library_Management_System.entity.BorrowRecord;
import com.example.Library_Management_System.entity.BorrowStatus;
import com.example.Library_Management_System.entity.Member;
import com.example.Library_Management_System.repository.BookRepository;
import com.example.Library_Management_System.repository.BorrowRecordRepository;
import com.example.Library_Management_System.repository.FineRepository;
import com.example.Library_Management_System.repository.MemberRepository;
import com.example.Library_Management_System.util.VarList;
import org.aspectj.weaver.ast.Var;
import org.hibernate.type.Type;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

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

    public ResponseDTO borrowBook(Long bookId){
        try{
            String email = getCurrentUserEmail();
            Optional<Member> memberOpt = memberRepository.findByMemEmail(email);

            if (!memberOpt.isPresent()){
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND,"Member not found",null,null);
            }

            Member member = memberOpt.get();

            Optional<Book> bookOpt = bookRepository.findById(bookId);
            if(!bookOpt.isPresent()){
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND,"Book not found",null,null);

            }

            Book book = bookOpt.get();

            //check if book is available
            if(!book.getBookAvailable()|| book.getAvailableCopies()<=0){
                return new ResponseDTO(VarList.RSP_ERROR,"Book is not available for borrowing",null,null);
            }

            //check if member already borrowed this book
            if (borrowRecordRepository.existsByMemberAndBookAndStatus(member,book, BorrowStatus.BORROWED)){
                return new ResponseDTO(VarList.RSP_ERROR,"You have already borrowed this book",null,null);
            }

            //create borrow record
            BorrowRecord borrowRecord = new BorrowRecord();
            borrowRecord.setMember(member);
            borrowRecord.setBook(book);
            borrowRecord.setBorrowDate(LocalDate.now().plusDays(14));
            borrowRecord.setStatus(BorrowStatus.BORROWED);


            //Update book availability
            book.setAvailableCopies(book.getAvailableCopies()-1);
            if (book.getAvailableCopies()<=0){
                book.setBookAvailable(false);
            }

            bookRepository.save(book);

            BorrowRecord saveRecord = borrowRecordRepository.save(borrowRecord);

            BorrowRecordDTO saveRecordDTO = modelMapper.map(saveRecord,BorrowRecordDTO.class);


            return new ResponseDTO(VarList.RSP_SUCCESS,"Book borrowed successfully Due date : "+ saveRecordDTO.getDueDate(),saveRecordDTO,null);

        }catch (Exception e){
            return new ResponseDTO(VarList.RSP_ERROR,"Failed to borrow book : "+e.getMessage(),null,null);
        }
    }

    public ResponseDTO getMyBorrowedBooks(){
        try{
            String email = getCurrentUserEmail();
            Optional<Member> memberOpt = memberRepository.findByMemEmail(email);

            if(!memberOpt.isPresent()){
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND,"Member not found",null,null);

            }

            List<BorrowRecord> borrowRecords = borrowRecordRepository.findByMemberAndStatus(memberOpt.get(),BorrowStatus.BORROWED);

            List<BorrowRecordDTO> borrowDTOs = modelMapper.map(borrowRecords, new TypeToken<List<BorrowRecordDTO>>(){}.getType());

            return new ResponseDTO(VarList.RSP_SUCCESS,"Borrowed books retrieved successfully",borrowDTOs,null);
        }catch (Exception e){
            return new ResponseDTO(VarList.RSP_ERROR,"Failed to retrieve borrowed books : " + e.getMessage(),null,null);
        }
    }

    public ResponseDTO getAllBorrowedBooks() {
        try {
            List<BorrowRecord> borrowRecords = borrowRecordRepository.findByStatus(BorrowStatus.BORROWED);

            List<BorrowRecordDTO> borrowDTOs = modelMapper.map(borrowRecords, new TypeToken<List<BorrowRecordDTO>>(){}.getType());

            return new ResponseDTO(VarList.RSP_SUCCESS, "Borrowed books retrieved successfully", borrowDTOs, null);

        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve borrowed books: " + e.getMessage(), null, null);
        }
    }

    public ResponseDTO checkOverdueBooks() {
        try {
            LocalDate today = LocalDate.now();
            List<BorrowRecord> overdueRecords = borrowRecordRepository.findByDueDateBeforeAndStatus(today, BorrowStatus.BORROWED);

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
            }

            List<BorrowRecordDTO> overdueDTOs = modelMapper.map(overdueRecords, new TypeToken<List<BorrowRecordDTO>>(){}.getType());

            return new ResponseDTO(VarList.RSP_SUCCESS, "Overdue books checked successfully", overdueDTOs, null);

        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to check overdue books: " + e.getMessage(), null, null);
        }
    }

    // Removed the manual convertToDTO method as it's replaced by ModelMapper

    private String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    public ResponseDTO returnBook(Long borrowId) {
        try {
            Optional<BorrowRecord> borrowRecordOpt = borrowRecordRepository.findById(borrowId);

            if (!borrowRecordOpt.isPresent()) {
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND, "Borrow record not found", null, null);
            }

            BorrowRecord borrowRecord = borrowRecordOpt.get();

            // Check if the book is already returned
            if (borrowRecord.getStatus() == BorrowStatus.RETURNED) {
                return new ResponseDTO(VarList.RSP_ERROR, "Book already returned", null, null);
            }

            LocalDate returnDate = LocalDate.now();
            borrowRecord.setReturnDate(returnDate);
            borrowRecord.setStatus(BorrowStatus.RETURNED);

            // Calculate fine if overdue
            if (returnDate.isAfter(borrowRecord.getDueDate())) {
                long daysOverdue = ChronoUnit.DAYS.between(borrowRecord.getDueDate(), returnDate);
                double fineAmount = daysOverdue * 10.0; // Rs. 10 per day
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
            if (borrowRecord.getFineAmount() != null && borrowRecord.getFineAmount() > 0) {
                message += String.format(" with a fine of Rs. %.2f", borrowRecord.getFineAmount());
            }

            return new ResponseDTO(VarList.RSP_SUCCESS, message, updatedRecordDTO, null);

        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to return book: " + e.getMessage(), null, null);
        }
    }
}

