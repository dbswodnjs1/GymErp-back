package com.example.gymerp.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.gymerp.dto.PtRegistrationDto;
import com.example.gymerp.service.PtRegistrationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/pt")
@RequiredArgsConstructor
public class PtController {

    private final PtRegistrationService ptService;
    
    // PT 예약 전체 목록 (필터: 트레이너(empNum), 날짜)
    @GetMapping("/list")
    public List<PtRegistrationDto> getAllPt(
        @RequestParam(required = false) Integer empNum,   // 트레이너 번호
        @RequestParam(required = false) String date       // 날짜 (optional)
    ) {
        return ptService.getAllPtRegistration(empNum, date);
    }

    // PT 예약 상세 조회
    @GetMapping("/{regNum}")
    public PtRegistrationDto getPtById(@PathVariable int regNum) {
        return ptService.getPtRegistrationById(regNum);
    }
    
    // PT 예약 등록
    @PostMapping
    public String insertPt(@RequestBody PtRegistrationDto dto) {
        ptService.insertPtRegistration(dto);
        return "success";
    }

    // PT 예약 수정
    @PutMapping("/{regNum}")
    public String updatePt(@PathVariable int regNum, @RequestBody PtRegistrationDto dto) {
    	dto.setRegNum((long) regNum); // int → long 변환
        ptService.updatePtRegistration(dto);
        return "success";
    }

    // PT 예약 삭제
    @DeleteMapping("/{regNum}")
    public String deletePt(@PathVariable int regNum) {
        ptService.deletePtRegistration(regNum);
        return "success";
    }
}
