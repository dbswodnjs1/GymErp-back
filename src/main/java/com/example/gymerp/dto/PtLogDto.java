package com.example.gymerp.dto;

import java.time.LocalDateTime;
import org.apache.ibatis.type.Alias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PT 이용내역 로그 DTO
 *
 * 상태(status) 정의:
 *  - "충전" : PT 판매 시 충전 로그
 *  - "소비" : PT 이용 시 차감 로그
 *  - "변경" : 트레이너 변경 시 기존/신규 트레이너 간 이동 기록
 *  - "환불" : 판매 수정 시 횟수 또는 금액 감소분에 대한 보정 로그
 *
 * ※ 모든 로그 식별은 복합키 (memNum + totalAmount + createdAt) 로 처리.
 */
@Alias("PtLogDto")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PtLogDto {
	
    private Long usageId;            // 이용내역 고유번호 (PK)
    private Long memNum;             // 회원 번호 (FK)
    private Long empNum;             // 트레이너(직원) 번호 (FK)
   
    private String status;           // 상태 (충전 / 소비 / 환불)
    private Long countChange;     	 // 이용횟수 변화량 (+ 충전, - 차감, - 환불)
    private LocalDateTime createdAt; // 생성일자 (로그 발생 시각)
    private Long salesId;			 // 판매ID
    private Long regId; 			 // PT등록 ID
    
    private String usageType; // CONSUME, CANCEL 등
    private LocalDateTime usageDate;
}
