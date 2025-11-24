package com.example.gymerp.dto;

import java.time.LocalDateTime;

import org.apache.ibatis.type.Alias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Alias("SalesService")
@Builder 
@NoArgsConstructor 
@AllArgsConstructor
@Data
public class SalesService {
	private Long serviceSalesId;     // 서비스 판매 내역 ID (PK)
    private Long serviceId;          // 서비스 상품 코드 (FK -> SERVICE.serviceId)
    private String serviceName;      // 상품명
    private Long empNum;             // 직원 ID (FK -> employee.empNum)
    private Long memNum;             // 회원 ID (FK -> member.memNum)
    private Long refundId;			 // 환불 ID (FK -> pt_log.usageId )
    private Integer baseCount;       // 기본 횟수/기간
    private Integer actualCount;     // 실제 횟수/기간
    private Long discount;         	 // 할인액
    private Long baseAmount;         // 기본 총액
    private Long actualAmount;       // 실제 총액
    private String serviceType;      // 구분 (PT / 이용권 등)
    private String status;           // 상태
    private LocalDateTime createdAt; // 등록일
    private LocalDateTime updatedAt; // 수정일
}
