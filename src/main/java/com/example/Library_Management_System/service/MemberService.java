package com.example.Library_Management_System.service;


import com.example.Library_Management_System.dto.MemberDto;
import com.example.Library_Management_System.entity.Member;
import com.example.Library_Management_System.repository.MemberRepository;
import com.example.Library_Management_System.util.VarList;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MemberService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MemberRepository memberRepository;


    public String saveMember(MemberDto memberDto) {
        if (memberRepository.existByEmailIgnoreCase(memberDto.getMemEmail())) {
            return VarList.RSP_DUPLICATED;
        } else {
            memberDto.setMembershipDate(LocalDate.now());
            memberDto.setActive(true);

            memberRepository.save(modelMapper.map(memberDto, Member.class));
            return VarList.RSP_SUCCESS;
        }
    }

    public String updateMember(MemberDto memberDto){
        if(!memberRepository.existsById(memberDto.getMemID())){

            return VarList.RSP_NO_DATA_FOUND;

        } else if (memberRepository.existsByEmailIgnoreCaseAndMemberIDNot(memberDto.getMemEmail(), memberDto.getMemID())) {

            return VarList.RSP_DUPLICATED;

        }else {

            memberRepository.save(modelMapper.map(memberDto,Member.class));
            return VarList.RSP_SUCCESS;
        }
    }

    public List<MemberDto> getAllMembers(){
        List<Member> members = memberRepository.findAll();
        return modelMapper.map(members,new TypeToken<List<MemberDto>>(){}.getType());
    }
    public String deleteMember(Long memID){
        if(memberRepository.existsById(memID)){
            memberRepository.deleteById(memID);
            return VarList.RSP_SUCCESS;
        }
        else {
            return VarList.RSP_NO_DATA_FOUND;
        }
    }
}
