package com.example.Library_Management_System.service;

import com.example.Library_Management_System.dto.FineDTO;
import com.example.Library_Management_System.dto.ResponseDTO;
import com.example.Library_Management_System.entity.BorrowRecord;
import com.example.Library_Management_System.entity.Fine;
import com.example.Library_Management_System.entity.FineStatus;
import com.example.Library_Management_System.entity.Member;
import com.example.Library_Management_System.repository.FineRepository;
import com.example.Library_Management_System.repository.MemberRepository;
import com.example.Library_Management_System.util.VarList;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FineService {

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Fine createFine(BorrowRecord borrowRecord) {
        Fine fine = new Fine();
        fine.setMember(borrowRecord.getMember());
        fine.setBorrowRecord(borrowRecord);
        fine.setAmount(borrowRecord.getFineAmount());
        fine.setStatus(FineStatus.UNPAID);

        return fineRepository.save(fine);
    }

    public ResponseDTO getMyFines() {
        try {
            String email = getCurrentUserEmail();
            Optional<Member> memberOpt = memberRepository.findByMemEmail(email);

            if (!memberOpt.isPresent()) {
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND, "Member not found", null, null);
            }

            List<Fine> fines = fineRepository.findByMember(memberOpt.get());
            List<FineDTO> fineDTOs = modelMapper.map(fines, new TypeToken<List<FineDTO>>(){}.getType());

            return new ResponseDTO(VarList.RSP_SUCCESS, "Fines retrieved successfully", fineDTOs, null);

        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve fines: " + e.getMessage(), null, null);
        }
    }

    public ResponseDTO getAllFines() {
        try {
            List<Fine> fines = fineRepository.findAll();
            List<FineDTO> fineDTOs = modelMapper.map(fines, new TypeToken<List<FineDTO>>(){}.getType());

            return new ResponseDTO(VarList.RSP_SUCCESS, "All fines retrieved successfully", fineDTOs, null);

        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve fines: " + e.getMessage(), null, null);
        }
    }

    public ResponseDTO getFinesByStatus(FineStatus status) {
        try {
            List<Fine> fines = fineRepository.findByStatus(status);
            List<FineDTO> fineDTOs = modelMapper.map(fines, new TypeToken<List<FineDTO>>(){}.getType());

            return new ResponseDTO(VarList.RSP_SUCCESS, "Fines retrieved successfully", fineDTOs, null);

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
            List<FineDTO> fineDTOs = modelMapper.map(fines, new TypeToken<List<FineDTO>>(){}.getType());

            return new ResponseDTO(VarList.RSP_SUCCESS, "Member fines retrieved successfully", fineDTOs, null);

        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve member fines: " + e.getMessage(), null, null);
        }
    }

    public ResponseDTO payFine(Long fineId) {
        try {
            Optional<Fine> fineOpt = fineRepository.findById(fineId);

            if (!fineOpt.isPresent()) {
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND, "Fine not found", null, null);
            }

            Fine fine = fineOpt.get();
            fine.setStatus(FineStatus.PAID);

            Fine paidFine = fineRepository.save(fine);
            FineDTO paidFineDTO = modelMapper.map(paidFine, FineDTO.class);

            return new ResponseDTO(VarList.RSP_SUCCESS, "Fine paid successfully", paidFineDTO, null);

        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to pay fine: " + e.getMessage(), null, null);
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
            FineDTO waivedFineDTO = modelMapper.map(waivedFine, FineDTO.class);

            return new ResponseDTO(VarList.RSP_SUCCESS, "Fine waived successfully", waivedFineDTO, null);

        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to waive fine: " + e.getMessage(), null, null);
        }
    }

    public ResponseDTO getFineByBorrowId(Long borrowId) {
        try {
            Fine fine = fineRepository.findByBorrowRecord_BorrowId(borrowId);

            if (fine == null) {
                return new ResponseDTO(VarList.RSP_NO_DATA_FOUND, "Fine not found for this borrow record", null, null);
            }

            FineDTO fineDTO = modelMapper.map(fine, FineDTO.class);

            return new ResponseDTO(VarList.RSP_SUCCESS, "Fine retrieved successfully", fineDTO, null);

        } catch (Exception e) {
            return new ResponseDTO(VarList.RSP_ERROR, "Failed to retrieve fine: " + e.getMessage(), null, null);
        }
    }

    private String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }
}