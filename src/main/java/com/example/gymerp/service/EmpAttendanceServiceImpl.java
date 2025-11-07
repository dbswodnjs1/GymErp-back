// src/main/java/com/example/gymerp/service/EmpAttendanceServiceImpl.java
package com.example.gymerp.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.gymerp.dto.EmpAttendanceDto;
import com.example.gymerp.repository.EmpAttendanceDao;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EmpAttendanceServiceImpl implements EmpAttendanceService {

    private final EmpAttendanceDao dao;

    
 // EmpAttendanceServiceImpl.java
    @Override
    public List<EmpAttendanceDto> getAllByDate(Date date) {
        return dao.selectAllByDate(date);
    }

    
    @Override
    public List<EmpAttendanceDto> getAllEmpAttendances() {
        return dao.selectAllEmpAttendances();
    }

    @Override
    public List<EmpAttendanceDto> getEmpAttendancesByEmpNum(int empNum) {
        return dao.selectEmpAttendancesByEmpNum(empNum);
    }

    @Override
    public EmpAttendanceDto getEmpAttendanceById(int attNum) {
        return dao.selectEmpAttendanceById(attNum);
    }

    public void checkIn(int empNum) {
        EmpAttendanceDto dto = new EmpAttendanceDto();
        dto.setEmpNum(empNum);
        dto.setAttState("WORKING");
        dao.insertEmpAttendance(dto); // ❗ return 쓰지 말고 그냥 호출 (Mapper에서 SYSDATE/SYSTIMESTAMP 처리)
    }

    // 컨트롤러가 service.checkIn(empNum, nowKst) 로 호출할 때 (애플리케이션에서 시각 확정 전달)
    @Override
    public void checkIn(int empNum, Timestamp now) {
        EmpAttendanceDto dto = new EmpAttendanceDto();
        dto.setEmpNum(empNum);
        dto.setAttState("WORKING");
        // 선택: now를 명시적으로 저장하고 싶으면 아래 두 줄 활성화 (Mapper의 NVL과 중복되지만 안전)
        dto.setCheckIn(now);
        dto.setAttDate(new Date(now.getTime()));
        dao.insertEmpAttendance(dto);
    }

    @Override
    public int updateEmpAttendanceCheckOut(int attNum, Timestamp checkOut) {
        return dao.updateEmpAttendanceCheckOut(attNum, checkOut);
    }

    @Override
    public int updateEmpAttendance(EmpAttendanceDto dto) {
        return dao.updateEmpAttendance(dto);
    }

    @Override
    public int deleteEmpAttendance(int attNum) {
        return dao.deleteEmpAttendance(attNum);
    }

    @Override
    public List<EmpAttendanceDto> getEmpAttendancesByRange(int empNum, Date from, Date to) {
        // empNum<=0 이면 "전직원 기간조회"로 Mapper의 selectAllByRange 사용
        if (empNum <= 0) {
            return dao.selectAllByRange(from, to); // ✅ 전직원용 DAO 메서드 호출
        }
        return dao.selectEmpAttendancesByRange(empNum, from, to);
    }
}
