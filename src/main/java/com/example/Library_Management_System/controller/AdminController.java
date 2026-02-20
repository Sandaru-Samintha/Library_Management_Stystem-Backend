package com.example.Library_Management_System.controller;


import com.example.Library_Management_System.dto.AdminDTO;
import com.example.Library_Management_System.dto.ResponseDTO;
import com.example.Library_Management_System.entity.BorrowStatus;
import com.example.Library_Management_System.entity.FineStatus;
import com.example.Library_Management_System.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;


    //===============Admin Profile ============================

    @GetMapping("/profile")
    public ResponseEntity<ResponseDTO> getAdminProfile(){
        ResponseDTO responseDTO = adminService.getAdminProfile();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
    @PutMapping("/profile/update")
    public ResponseEntity<ResponseDTO> updateAdminProfile(@ModelAttribute AdminDTO adminDTO){
        ResponseDTO responseDTO = adminService.updateAdminProfile(adminDTO);
        return  new ResponseEntity<>(responseDTO,HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDTO> createAdmin(@ModelAttribute AdminDTO adminDTO){
        ResponseDTO responseDTO = adminService.createAdmin(adminDTO);
        return new ResponseEntity<>(responseDTO,HttpStatus.OK);
    }


    //==================== Dashboard ===========================

    @GetMapping("/dashboard/status")
    public ResponseEntity<ResponseDTO> getDashboardStatus(){
        ResponseDTO responseDTO = adminService.getDashboardStats();
        return  new ResponseEntity<>(responseDTO,HttpStatus.OK);
    }

    //================Member management ===========================

    @GetMapping("/members/all")
    public ResponseEntity<ResponseDTO> getAllMembers(){
        ResponseDTO responseDTO = adminService.getAllMembers();
        return  new ResponseEntity<>(responseDTO,HttpStatus.OK);
    }

    @GetMapping("/members/{memberId}")
    public ResponseEntity<ResponseDTO> getMemberId(@PathVariable Long memberId){
        ResponseDTO responseDTO = adminService.getMemberById(memberId);
        return new ResponseEntity<>(responseDTO,HttpStatus.OK);
    }
    @PutMapping("/members/{memberId}/status")
    public ResponseEntity<ResponseDTO>updateMemberStatus(@PathVariable Long memberId, @RequestParam boolean active){
        ResponseDTO responseDTO= adminService.updateMemberStatus(memberId,active);
        return new ResponseEntity<>(responseDTO,HttpStatus.OK);
    }

    @GetMapping("/members/search")
    public ResponseEntity<ResponseDTO> searchMembers(@RequestParam String keyword){
        ResponseDTO responseDTO = adminService.searchMembers(keyword);
        return new ResponseEntity<>(responseDTO,HttpStatus.OK);
    }

    //======================Book Management ===========================

    @PutMapping("/books/{bookId}/availability")
    public ResponseEntity<ResponseDTO> updateBookAvailability(@PathVariable Long bookId, @RequestParam Integer totalCopies){
        ResponseDTO responseDTO = adminService.updateBookAvailability(bookId,totalCopies);
        return  new ResponseEntity<>(responseDTO,HttpStatus.OK);
    }

    // ==================== BORROWING MANAGEMENT ====================

    @GetMapping("/borrows/all")
    public ResponseEntity<ResponseDTO> getAllBorrowRecords() {
        ResponseDTO responseDTO = adminService.getAllBorrowRecords();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/borrows/status/{status}")
    public ResponseEntity<ResponseDTO> getBorrowRecordsByStatus(@PathVariable BorrowStatus status) {
        ResponseDTO responseDTO = adminService.getBorrowRecordsByStatus(status);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/borrows/member/{memberId}")
    public ResponseEntity<ResponseDTO> getMemberBorrowHistory(@PathVariable Long memberId) {
        ResponseDTO responseDTO = adminService.getMemberBorrowHistory(memberId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/borrows/book/{bookId}")
    public ResponseEntity<ResponseDTO> getBookBorrowHistory(@PathVariable Long bookId) {
        ResponseDTO responseDTO = adminService.getBookBorrowHistory(bookId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PutMapping("/borrows/{borrowId}/extend")
    public ResponseEntity<ResponseDTO> extendDueDate(
            @PathVariable Long borrowId,
            @RequestParam int additionalDays) {
        ResponseDTO responseDTO = adminService.extendDueDate(borrowId, additionalDays);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    // ==================== FINE MANAGEMENT ====================

    @GetMapping("/fines/all")
    public ResponseEntity<ResponseDTO> getAllFines() {
        ResponseDTO responseDTO = adminService.getAllFines();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/fines/status/{status}")
    public ResponseEntity<ResponseDTO> getFinesByStatus(@PathVariable FineStatus status) {
        ResponseDTO responseDTO = adminService.getFinesByStatus(status);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/fines/member/{memberId}")
    public ResponseEntity<ResponseDTO> getMemberFines(@PathVariable Long memberId) {
        ResponseDTO responseDTO = adminService.getMemberFines(memberId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PutMapping("/fines/{fineId}/waive")
    public ResponseEntity<ResponseDTO> waiveFine(@PathVariable Long fineId) {
        ResponseDTO responseDTO = adminService.waiveFine(fineId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    // ==================== REPORTS ====================

    @GetMapping("/reports/monthly")
    public ResponseEntity<ResponseDTO> generateMonthlyReport(
            @RequestParam int year,
            @RequestParam int month) {
        ResponseDTO responseDTO = adminService.generateMonthlyReport(year, month);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/reports/member/{memberId}")
    public ResponseEntity<ResponseDTO> generateMemberActivityReport(@PathVariable Long memberId) {
        ResponseDTO responseDTO = adminService.generateMemberActivityReport(memberId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

}
