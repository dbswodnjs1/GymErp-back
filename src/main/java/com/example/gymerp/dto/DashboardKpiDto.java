// src/main/java/com/example/gymerp/dto/DashboardKpiDto.java
package com.example.gymerp.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class DashboardKpiDto {
    // 활성 회원: voucher_log.endDate >= 오늘 (필요시 startDate <= 오늘 추가)
    private long activeMembers;

    // 이번 달 신규 회원 (member.createdAt 기준 - 스키마에 맞게 컬럼명 확인)
    private long monthNewMembers;

    // 월 누적 매출 (서비스+상품 합)
    private BigDecimal mtdRevenue;
}
