package com.example.gymerp.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockAdjustmentDto {
	private int adjustmentId;      // 출고(조정) PK
    private int productId;         // 상품 ID (FK)
    private String codeBId;        // 세부 코드 ID (FK)
    private LocalDateTime createdAt; // 생성일자
    private int quantity;   // 조정 수량 (차감 수량)
    private String notes;          // 비고 (파손, 분실 등 사유)

    // 조인용 필드 (선택)
    private String productName;    // Product.name
    private String codeBName;      // CodeB.name (출력용)
    private String codeAId;        // CodeAId (조인시 함께 쓰일 수 있음)
}
