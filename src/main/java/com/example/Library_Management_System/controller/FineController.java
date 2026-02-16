package com.example.Library_Management_System.controller;


import com.example.Library_Management_System.dto.ResponseDTO;
import com.example.Library_Management_System.service.FineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/fines")
public class FineController {

    @Autowired
    private FineService fineService;

    @GetMapping("/my-fines")
    @PreAuthorize("hasAuthority('MEMBER')")
    public ResponseEntity<ResponseDTO> getMyFines(){
        ResponseDTO responseDTO = fineService.getMyFines();
        return  new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }


    @GetMapping("/admin/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> getAllFines(){
        ResponseDTO responseDTO = fineService.getMyFines();
        return new ResponseEntity<>(responseDTO,HttpStatus.OK);
    }

    @GetMapping("/pay/{fineId}")
    @PreAuthorize("hasAuthority('MEMBER')")
    public ResponseEntity<ResponseDTO> payFine(@PathVariable Long fineId){
        ResponseDTO responseDTO = fineService.payFine(fineId);
        return new ResponseEntity<>(responseDTO,HttpStatus.OK);
    }
}
