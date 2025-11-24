package com.example.gymerp.dto;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.apache.ibatis.type.Alias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Alias("PtRegistrationDto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PtRegistrationDto {
    private Long regNum;          // 예약 번호 (PK)
    private Long empNum;          // 트레이너 번호 (FK → EMP)
    private Long memNum;          // 회원 번호 (FK → MEMBER)
    private Long shNum;           // 스케줄 번호 (FK → SCHEDULE)
    private Timestamp regCreated; // 예약 생성 일시
    private LocalDateTime regTime;         // PT 시작 시간
    private LocalDateTime lastTime;        // PT 종료 시간
    private Date regDate;         // 예약 일자
    private Timestamp regUpdatedAt; // 예약 수정 일시
    private String regNote;       // 비고
 // ------------------- PT 로그 연계용 필드 -------------------
    private Long usageId;         // PT_LOG PK (Mapper에서 selectKey로 자동 생성)
    private Long salesId;         // 판매 ID (기본은 NULL, 필요 시 연결)

    
}