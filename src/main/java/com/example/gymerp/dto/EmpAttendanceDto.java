package com.example.gymerp.dto;

import java.sql.Date;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpAttendanceDto {
    private int attNum;        // 근태번호 (PK)
    private int empNum;        // 직원번호 (FK)
    private Date attDate;       // 날짜
    private Timestamp checkIn;  // 출근 시간
    private Timestamp checkOut; // 퇴근 시간
    private int workHours;   // 근무시간
    private String attState;    // 상태
    
    // 🔥 추가
    private String empName;      // 직원명
}