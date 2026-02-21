package com.example.Library_Management_System.service;


import com.example.Library_Management_System.dto.AdminDTO;
import com.example.Library_Management_System.dto.ResponseDTO;
import com.example.Library_Management_System.entity.*;
import com.example.Library_Management_System.repository.*;
import com.example.Library_Management_System.util.FileUploadUtil;
import com.example.Library_Management_System.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @Autowired
    private ModelMapper modelMapper;

    // =================Admin Profile Management ====================
    public ResponseDTO getAdminProfile(){
        try{
            String email = getCurrentAdminEmail();
            Optional<Admin> adminOpt = adminRepository.findByAdminEmail(email);

            if(adminOpt.isPresent()){
                AdminDTO adminDTO = modelMapper.map(adminOpt.get(),AdminDTO.class);
                return new ResponseDTO(VarList.RSP_SUCCESS,"Admin profile retrieved successfully",adminDTO,null);
            }else{
                return  new ResponseDTO(VarList.RSP_NO_DATA_FOUND,"Admin not found",null,null);
            }
        }catch (Exception e){
            return  new ResponseDTO(VarList.RSP_ERROR,"Failed to retrieve admin profile : "+ e.getMessage(),null,null);
        }
    }


    public ResponseDTO updateAdminProfile(AdminDTO adminDTO){
        try{
            String email = getCurrentAdminEmail();
            Optional<Admin> adminOpt = adminRepository.findByAdminEmail(email);

            if(!adminOpt.isPresent()){
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND,"Admin not found",null,null);
            }

            Admin admin = adminOpt.get();
            admin.setAdminFullName(adminDTO.getAdminFullName());
            admin.setAdminPhoneNumber(adminDTO.getAdminPhoneNumber());
            admin.setAdminDepartment(adminDTO.getAdminDepartment());

            //Update password if provided
            if(adminDTO.getAdminPassword() != null && ! adminDTO.getAdminPassword().isEmpty()){
                admin.setAdminPassword(passwordEncoder.encode(adminDTO.getAdminPassword()));
            }

            // Handle profile image upload
            if (adminDTO.getProfileImage() != null && !adminDTO.getProfileImage().isEmpty()) {
                // Delete old image if exists
                if (admin.getProfileImageUrl() != null) {
                    fileUploadUtil.deleteImage(admin.getProfileImageUrl());
                }
                String imageUrl = fileUploadUtil.saveAdminImage(adminDTO.getProfileImage());
                admin.setProfileImageUrl(imageUrl);
            }

            Admin updatedAdmin = adminRepository.save(admin);
            AdminDTO updatedAdminDTO = modelMapper.map(updatedAdmin,AdminDTO.class);
            return  new ResponseDTO(VarList.RSP_SUCCESS,"Admin profile updated successfully",updatedAdminDTO,null);

        } catch (Exception e) {
            return  new ResponseDTO(VarList.RSP_ERROR,"Failed to update admin profile : " + e.getMessage(),null,null);
        }
    }

    //======================= Dashboard statistics ========================

    public ResponseDTO getDashboardStats(){
        try{
            Map<String,Object> stats = new HashMap<>();

            //Book statistics
            long totalBooks = bookRepository.count();
            long availableBooks = bookRepository.findByBookAvailableTrue().size();
            long borrowedBooks = totalBooks - availableBooks;

            //Member statistics
            long totalMembers = memberRepository.count();
            long activeMembers = memberRepository.findAll().stream().filter(Member::isActive).count();

            //Borrow statistics
            long currentBorrows = borrowRecordRepository.findByStatus(BorrowStatus.BORROWED).size();
            long overdueBorrows = borrowRecordRepository.findByStatus(BorrowStatus.OVERDUE).size();

            //Fine statistics
            double totalFinesCollected = fineRepository.findAll().stream()
                    .filter(f->f.getStatus() == FineStatus.PAID)
                    .mapToDouble(Fine::getAmount)
                    .sum();

            double pendingFines = fineRepository.findAll().stream()
                    .filter(f->f.getStatus()==FineStatus.UNPAID)
                    .mapToDouble(Fine::getAmount)
                    .sum();

            //Today's activities
            LocalDate today = LocalDate.now();
            long todayBorrows = borrowRecordRepository.findAll().stream()
                    .filter(b->b.getBorrowDate().equals(today))
                    .count();

            long todayReturns = borrowRecordRepository.findAll().stream()
                    .filter(b->b.getReturnDate() !=null && b.getReturnDate().equals(today))
                    .count();

            stats.put("totalBooks", totalBooks);
            stats.put("availableBooks", availableBooks);
            stats.put("borrowedBooks", borrowedBooks);
            stats.put("totalMembers", totalMembers);
            stats.put("activeMembers", activeMembers);
            stats.put("currentBorrows", currentBorrows);
            stats.put("overdueBorrows", overdueBorrows);
            stats.put("totalFinesCollected", totalFinesCollected);
            stats.put("pendingFines", pendingFines);
            stats.put("todayBorrows", todayBorrows);
            stats.put("todayReturns", todayReturns);

            return new ResponseDTO(VarList.RSP_SUCCESS, "Dashboard statistics retrieved successfully", stats, null);

        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve dashboard statistics: " + e.getMessage(), null, null);
        }
    }

    // ==================== MEMBER MANAGEMENT ====================

    public ResponseDTO getAllMembers() {
        try {
            List<Member> members = memberRepository.findAll();
            // Using ModelMapper to convert List<Member> to List<?> (keeping as entities for consistency)
            return new ResponseDTO(VarList.RSP_SUCCESS, "Members retrieved successfully", members, null);
        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve members: " + e.getMessage(), null, null);
        }
    }

    public ResponseDTO getMemberById(Long memberId) {
        try {
            Optional<Member> memberOpt = memberRepository.findById(memberId);
            if (memberOpt.isPresent()) {
                return new ResponseDTO(VarList.RSP_SUCCESS, "Member found", memberOpt.get(), null);
            } else {
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND, "Member not found", null, null);
            }
        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve member: " + e.getMessage(), null, null);
        }
    }

    public ResponseDTO updateMemberStatus(Long memberId, boolean active) {
        try {
            Optional<Member> memberOpt = memberRepository.findById(memberId);
            if (!memberOpt.isPresent()) {
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND, "Member not found", null, null);
            }

            Member member = memberOpt.get();
            member.setActive(active);

            // If deactivating member, check for borrowed books
            if (!active) {
                List<BorrowRecord> activeBorrows = borrowRecordRepository
                        .findByMemberAndStatus(member, BorrowStatus.BORROWED);
                if (!activeBorrows.isEmpty()) {
                    return new ResponseDTO(VarList.RSP_ERROR,
                            "Cannot deactivate member with active borrows. Please ensure all books are returned first.",
                            null, null);
                }
            }

            Member updatedMember = memberRepository.save(member);
            String status = active ? "activated" : "deactivated";
            return new ResponseDTO(VarList.RSP_SUCCESS, "Member " + status + " successfully", updatedMember, null);

        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to update member status: " + e.getMessage(), null, null);
        }
    }

    public ResponseDTO searchMembers(String keyword) {
        try {
            List<Member> members = memberRepository.findAll().stream()
                    .filter(m -> m.getMemFullName().toLowerCase().contains(keyword.toLowerCase()) ||
                            m.getMemEmail().toLowerCase().contains(keyword.toLowerCase()) ||
                            (m.getMemPhoneNumber() != null && m.getMemPhoneNumber().contains(keyword)))
                    .collect(Collectors.toList());

            return new ResponseDTO(VarList.RSP_SUCCESS, "Members retrieved successfully", members, null);
        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to search members: " + e.getMessage(), null, null);
        }
    }

    // ==================== BOOK MANAGEMENT (Extended Admin Operations) ====================

    public ResponseDTO updateBookAvailability(Long bookId, Integer totalCopies) {
        try {
            Optional<Book> bookOpt = bookRepository.findById(bookId);
            if (!bookOpt.isPresent()) {
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND, "Book not found", null, null);
            }

            Book book = bookOpt.get();
            int currentBorrowed = book.getTotalCopies() - book.getAvailableCopies();

            if (totalCopies < currentBorrowed) {
                return new ResponseDTO(VarList.RSP_ERROR,
                        "Cannot reduce copies below currently borrowed count (" + currentBorrowed + ")",
                        null, null);
            }

            book.setTotalCopies(totalCopies);
            book.setAvailableCopies(totalCopies - currentBorrowed);
            book.setBookAvailable(book.getAvailableCopies() > 0);

            Book updatedBook = bookRepository.save(book);
            return new ResponseDTO(VarList.RSP_SUCCESS, "Book availability updated successfully", updatedBook, null);

        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to update book availability: " + e.getMessage(), null, null);
        }
    }

    // ==================== BORROWING MANAGEMENT ====================

    public ResponseDTO getAllBorrowRecords() {
        try {
            List<BorrowRecord> records = borrowRecordRepository.findAll();
            return new ResponseDTO(VarList.RSP_SUCCESS, "Borrow records retrieved successfully", records, null);
        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve borrow records: " + e.getMessage(), null, null);
        }
    }

    public ResponseDTO getBorrowRecordsByStatus(BorrowStatus status) {
        try {
            List<BorrowRecord> records = borrowRecordRepository.findByStatus(status);
            return new ResponseDTO(VarList.RSP_SUCCESS, "Borrow records retrieved successfully", records, null);
        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve borrow records: " + e.getMessage(), null, null);
        }
    }

    public ResponseDTO getMemberBorrowHistory(Long memberId) {
        try {
            Optional<Member> memberOpt = memberRepository.findById(memberId);
            if (!memberOpt.isPresent()) {
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND, "Member not found", null, null);
            }

            List<BorrowRecord> records = borrowRecordRepository.findByMember(memberOpt.get());
            return new ResponseDTO(VarList.RSP_SUCCESS, "Member borrow history retrieved successfully", records, null);

        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve borrow history: " + e.getMessage(), null, null);
        }
    }

    public ResponseDTO getBookBorrowHistory(Long bookId) {
        try {
            Optional<Book> bookOpt = bookRepository.findById(bookId);
            if (!bookOpt.isPresent()) {
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND, "Book not found", null, null);
            }

            List<BorrowRecord> records = borrowRecordRepository.findByBook(bookOpt.get());
            return new ResponseDTO(VarList.RSP_SUCCESS, "Book borrow history retrieved successfully", records, null);

        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve book borrow history: " + e.getMessage(), null, null);
        }
    }

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
            return new ResponseDTO(VarList.RSP_SUCCESS, "Due date extended successfully", updatedRecord, null);

        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to extend due date: " + e.getMessage(), null, null);
        }
    }

    // ==================== FINE MANAGEMENT ====================

    public ResponseDTO getAllFines() {
        try {
            List<Fine> fines = fineRepository.findAll();
            return new ResponseDTO(VarList.RSP_SUCCESS, "Fines retrieved successfully", fines, null);
        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve fines: " + e.getMessage(), null, null);
        }
    }

    public ResponseDTO getFinesByStatus(FineStatus status) {
        try {
            List<Fine> fines = fineRepository.findByStatus(status);
            return new ResponseDTO(VarList.RSP_SUCCESS, "Fines retrieved successfully", fines, null);
        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve fines: " + e.getMessage(), null, null);
        }
    }

    public ResponseDTO getMemberFines(Long memberId) {
        try {
            Optional<Member> memberOpt = memberRepository.findById(memberId);
            if (!memberOpt.isPresent()) {
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND, "Member not found", null, null);
            }

            List<Fine> fines = fineRepository.findByMember(memberOpt.get());
            return new ResponseDTO(VarList.RSP_SUCCESS, "Member fines retrieved successfully", fines, null);

        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve member fines: " + e.getMessage(), null, null);
        }
    }

    public ResponseDTO waiveFine(Long fineId) {
        try {
            Optional<Fine> fineOpt = fineRepository.findById(fineId);
            if (!fineOpt.isPresent()) {
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND, "Fine not found", null, null);
            }

            Fine fine = fineOpt.get();
            fine.setAmount(0.0);
            fine.setStatus(FineStatus.PAID);

            Fine waivedFine = fineRepository.save(fine);
            return new ResponseDTO(VarList.RSP_SUCCESS, "Fine waived successfully", waivedFine, null);

        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to waive fine: " + e.getMessage(), null, null);
        }
    }

    // ==================== REPORTS ====================

    public ResponseDTO generateMonthlyReport(int year, int month) {
        try {
            Map<String, Object> report = new HashMap<>();
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.plusMonths(1).minusDays(1);

            // Borrow statistics for the month
            List<BorrowRecord> monthlyBorrows = borrowRecordRepository.findAll().stream()
                    .filter(b -> !b.getBorrowDate().isBefore(startDate) && !b.getBorrowDate().isAfter(endDate))
                    .collect(Collectors.toList());

            long totalBorrows = monthlyBorrows.size();
            long totalReturns = monthlyBorrows.stream()
                    .filter(b -> b.getReturnDate() != null)
                    .count();

            // Most borrowed books
            Map<Book, Long> bookBorrowCount = monthlyBorrows.stream()
                    .collect(Collectors.groupingBy(BorrowRecord::getBook, Collectors.counting()));

            List<Map.Entry<Book, Long>> topBooks = bookBorrowCount.entrySet().stream()
                    .sorted(Map.Entry.<Book, Long>comparingByValue().reversed())
                    .limit(5)
                    .collect(Collectors.toList());

            // Fine collection for the month
            List<Fine> monthlyFines = fineRepository.findAll().stream()
                    .filter(f -> !f.getFineDate().isBefore(startDate) && !f.getFineDate().isAfter(endDate))
                    .collect(Collectors.toList());

            double totalFines = monthlyFines.stream()
                    .mapToDouble(Fine::getAmount)
                    .sum();

            double collectedFines = monthlyFines.stream()
                    .filter(f -> f.getStatus() == FineStatus.PAID)
                    .mapToDouble(Fine::getAmount)
                    .sum();

            report.put("reportPeriod", startDate + " to " + endDate);
            report.put("totalBorrows", totalBorrows);
            report.put("totalReturns", totalReturns);
            report.put("topBorrowedBooks", topBooks);
            report.put("totalFinesGenerated", totalFines);
            report.put("totalFinesCollected", collectedFines);
            report.put("pendingFines", totalFines - collectedFines);

            return new ResponseDTO(VarList.RSP_SUCCESS, "Monthly report generated successfully", report, null);

        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to generate monthly report: " + e.getMessage(), null, null);
        }
    }

    public ResponseDTO generateMemberActivityReport(Long memberId) {
        try {
            Optional<Member> memberOpt = memberRepository.findById(memberId);
            if (!memberOpt.isPresent()) {
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND, "Member not found", null, null);
            }

            Member member = memberOpt.get();
            Map<String, Object> report = new HashMap<>();

            List<BorrowRecord> memberBorrows = borrowRecordRepository.findByMember(member);
            List<Fine> memberFines = fineRepository.findByMember(member);

            long totalBorrows = memberBorrows.size();
            long currentBorrows = memberBorrows.stream()
                    .filter(b -> b.getStatus() == BorrowStatus.BORROWED)
                    .count();

            long overdueBorrows = memberBorrows.stream()
                    .filter(b -> b.getStatus() == BorrowStatus.OVERDUE)
                    .count();

            double totalFines = memberFines.stream()
                    .mapToDouble(Fine::getAmount)
                    .sum();

            double paidFines = memberFines.stream()
                    .filter(f -> f.getStatus() == FineStatus.PAID)
                    .mapToDouble(Fine::getAmount)
                    .sum();

            report.put("memberName", member.getMemFullName());
            report.put("memberEmail", member.getMemEmail());
            report.put("membershipDate", member.getMembershipDate());
            report.put("totalBorrows", totalBorrows);
            report.put("currentBorrows", currentBorrows);
            report.put("overdueBorrows", overdueBorrows);
            report.put("totalFines", totalFines);
            report.put("paidFines", paidFines);
            report.put("pendingFines", totalFines - paidFines);

            return new ResponseDTO(VarList.RSP_SUCCESS, "Member activity report generated successfully", report, null);

        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to generate member activity report: " + e.getMessage(), null, null);
        }
    }

    // ==================== UTILITY METHODS ====================

    private String getCurrentAdminEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    public ResponseDTO createAdmin(AdminDTO adminDTO) {
        try {
            // Add debug logs
            System.out.println("=== Creating Admin ===");
            System.out.println("Email: " + adminDTO.getAdminEmail());
            System.out.println("Password: " + (adminDTO.getAdminPassword() != null ? "provided" : "NULL!"));
            System.out.println("Name: " + adminDTO.getAdminFullName());
            System.out.println("Has file: " + (adminDTO.getProfileImage() != null ? "yes" : "no"));

            if (adminDTO.getAdminPassword() == null || adminDTO.getAdminPassword().isEmpty()) {
                return new ResponseDTO(VarList.RSP_ERROR, "Password cannot be empty", null, null);
            }

            if (adminRepository.existsByAdminEmail(adminDTO.getAdminEmail())) {
                return new ResponseDTO(VarList.RSP_DUPLICATED, "Admin with this email already exists", null, null);
            }

            Admin admin = modelMapper.map(adminDTO, Admin.class);
            admin.setAdminPassword(passwordEncoder.encode(adminDTO.getAdminPassword()));
            admin.setRole(Role.ADMIN);

            // Handle profile image upload
            if (adminDTO.getProfileImage() != null && !adminDTO.getProfileImage().isEmpty()) {
                System.out.println("Uploading image: " + adminDTO.getProfileImage().getOriginalFilename());
                String imageUrl = fileUploadUtil.saveAdminImage(adminDTO.getProfileImage());
                admin.setProfileImageUrl(imageUrl);
                System.out.println("Image saved at: " + imageUrl);
            }

            Admin savedAdmin = adminRepository.save(admin);
            System.out.println("Admin saved with ID: " + savedAdmin.getAdminId());

            AdminDTO savedAdminDTO = modelMapper.map(savedAdmin, AdminDTO.class);
            return new ResponseDTO(VarList.RSP_SUCCESS, "Admin created successfully", savedAdminDTO, null);

        } catch (Exception e) {
            e.printStackTrace(); // Print full stack trace
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to create admin: " + e.getMessage(), null, null);
        }
    }
}

