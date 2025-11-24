package com.example.gymerp.dto;

import java.time.LocalDateTime;

import org.apache.ibatis.type.Alias;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Alias("ScheduleDto")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ScheduleDto {
    //-------------------DB 매핑 필드---------------------
    private Long shNum;           // 스케줄 고유번호 (PK)
    private Long empNum;          // 직원 고유번호 (FK)
    private String codeBid;       // 일정유형(PT, VACATION, ETC)

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startTime;   // 시작시간

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")

    private LocalDateTime endTime;     // 종료시간

    private String memo;               // 메모

    //-------------------조회/출력용 필드---------------------
    private String codeBName;          // 일정유형 이름 (CodeB 조인 결과)
    private String empName;            // 트레이너 이름
    private String memName;            // 회원 이름 (PT 전용)
    private String refType;            // 개인일정/공통일정 구분
    private Long memNum;               // 회원 번호
    //  PT 전용 필드 추가
    private Long regId;

}