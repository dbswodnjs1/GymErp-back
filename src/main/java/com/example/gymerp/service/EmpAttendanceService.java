// src/main/java/com/example/gymerp/service/EmpAttendanceService.java
package com.example.gymerp.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import com.example.gymerp.dto.EmpAttendanceDto;

public interface EmpAttendanceService {
	
	// EmpAttendanceService.java
	List<EmpAttendanceDto> getAllByDate(Date date);

	
    List<EmpAttendanceDto> getAllEmpAttendances();
    List<EmpAttendanceDto> getEmpAttendancesByEmpNum(int empNum);
    EmpAttendanceDto getEmpAttendanceById(int attNum);

    // ✅ 바디 DTO 대신 확정된 empNum만 받음
    void checkIn(int empNum, Timestamp now);

    int updateEmpAttendanceCheckOut(int attNum, Timestamp checkOut);
    int updateEmpAttendance(EmpAttendanceDto dto);
    int deleteEmpAttendance(int attNum);

    // ✅ 범위조회(전직원/특정직원)
    List<EmpAttendanceDto> getEmpAttendancesByRange(int empNum, Date from, Date to);
}
