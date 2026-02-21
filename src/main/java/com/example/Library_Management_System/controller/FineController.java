package com.example.Library_Management_System.controller;

import com.example.Library_Management_System.dto.ResponseDTO;
import com.example.Library_Management_System.entity.FineStatus;
import com.example.Library_Management_System.service.FineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fines")
@CrossOrigin
public class FineController {

    @Autowired
    private FineService fineService;

    @GetMapping("/my-fines")
    @PreAuthorize("hasAuthority('MEMBER')")
    public ResponseEntity<ResponseDTO> getMyFines() {
        ResponseDTO responseDTO = fineService.getMyFines();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getAllFines() {
        ResponseDTO responseDTO = fineService.getAllFines();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/admin/status/{status}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getFinesByStatus(@PathVariable FineStatus status) {
        ResponseDTO responseDTO = fineService.getFinesByStatus(status);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/admin/member/{memberId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getMemberFines(@PathVariable Long memberId) {
        ResponseDTO responseDTO = fineService.getMemberFines(memberId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/borrow/{borrowId}")
    @PreAuthorize("hasAnyAuthority('MEMBER', 'ADMIN')")
    public ResponseEntity<ResponseDTO> getFineByBorrowId(@PathVariable Long borrowId) {
        ResponseDTO responseDTO = fineService.getFineByBorrowId(borrowId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping("/pay/{fineId}")
    @PreAuthorize("hasAuthority('MEMBER')")
    public ResponseEntity<ResponseDTO> payFine(@PathVariable Long fineId) {
        ResponseDTO responseDTO = fineService.payFine(fineId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PutMapping("/admin/waive/{fineId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> waiveFine(@PathVariable Long fineId) {
        ResponseDTO responseDTO = fineService.waiveFine(fineId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}