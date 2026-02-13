package com.example.Library_Management_System.controller;

import com.example.Library_Management_System.dto.MemberDto;
import com.example.Library_Management_System.dto.ResponseDto;
import com.example.Library_Management_System.service.MemberService;
import com.example.Library_Management_System.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/member")
@CrossOrigin
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private ResponseDto responseDto;

    @PostMapping("/saveMember")
    public ResponseEntity saveMember(@RequestBody MemberDto memberDto){
        try{
            String res = memberService.saveMember(memberDto);

            if(res.equals("00")){
                responseDto.setCode(VarList.RSP_SUCCESS);
                responseDto.setMessage("Member Registered Successfully");
                responseDto.setContent(memberDto);

                return new ResponseEntity(responseDto, HttpStatus.CREATED);
            } else if (res.equals("06")) {
                responseDto.setCode(VarList.RSP_DUPLICATED);
                responseDto.setMessage("Email is already exists");
                responseDto.setContent(memberDto);

                return  new ResponseEntity(responseDto,HttpStatus.BAD_REQUEST);
            }
            else {
                responseDto.setCode(VarList.RSP_FAIL);
                responseDto.setMessage("Error");
                responseDto.setContent(null);

                return  new ResponseEntity(responseDto,HttpStatus.BAD_REQUEST);
            }

        }catch (Exception ex){
            responseDto.setCode(VarList.RSP_FAIL);
            responseDto.setMessage(ex.getMessage());
            responseDto.setContent(null);

            return new ResponseEntity(responseDto,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getAllMembers")
    public ResponseEntity getAllMembers(){
        try {
            List<MemberDto> memberDtoList = memberService.getAllMembers();

            responseDto.setCode(VarList.RSP_SUCCESS);
            responseDto.setMessage("Success");
            responseDto.setContent(memberDtoList);

            return  new ResponseEntity(responseDto,HttpStatus.OK);
        }catch (Exception ex){
            responseDto.setCode(VarList.RSP_FAIL);
            responseDto.setMessage(ex.getMessage());
            responseDto.setContent(null);

            return  new ResponseEntity(responseDto,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updateMember")
    public ResponseEntity updateMember(@RequestBody MemberDto memberDto){
        try {
            String res = memberService.updateMember(memberDto);

            if(res.equals("00")){
                responseDto.setCode(VarList.RSP_SUCCESS);
                responseDto.setMessage("Member update successfully");
                responseDto.setContent(memberDto);

                return new ResponseEntity(responseDto,HttpStatus.OK);
            } else if (res.equals("06")) {
                responseDto.setCode(VarList.RSP_DUPLICATED);
                responseDto.setMessage("Email is already exists");
                responseDto.setContent(memberDto);

                return  new ResponseEntity(responseDto,HttpStatus.BAD_REQUEST);
            } else if (res.equals("01")) {
                responseDto.setCode(VarList.RSP_NO_DATA_FOUND);
                responseDto.setMessage("Member nt found");
                responseDto.setContent(null);

                return  new ResponseEntity(responseDto,HttpStatus.NOT_FOUND);
            }else {
                responseDto.setCode(VarList.RSP_FAIL);
                responseDto.setMessage("Update Fail");
                responseDto.setContent(null);

                return  new ResponseEntity(responseDto,HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex){
            responseDto.setCode(VarList.RSP_FAIL);
            responseDto.setMessage(ex.getMessage());
            responseDto.setContent(null);

            return  new ResponseEntity(responseDto,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteMember/{memID}")
    public ResponseEntity deleteMember(@PathVariable Long memID){
        try{
            String res = memberService.deleteMember(memID);

            if(res.equals("00")){
                responseDto.setCode(VarList.RSP_SUCCESS);
                responseDto.setMessage("Member Deleted");
                responseDto.setContent(memID);

                return  new ResponseEntity(responseDto,HttpStatus.OK);

            }
            else {
                responseDto.setCode(VarList.RSP_FAIL);
                responseDto.setMessage("Member not found");
                responseDto.setContent(null);

                return  new ResponseEntity(responseDto,HttpStatus.NOT_FOUND);
            }
        }catch (Exception ex){
            responseDto.setCode(VarList.RSP_FAIL);
            responseDto.setMessage(ex.getMessage());
            responseDto.setContent(null);

            return  new ResponseEntity(responseDto,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
