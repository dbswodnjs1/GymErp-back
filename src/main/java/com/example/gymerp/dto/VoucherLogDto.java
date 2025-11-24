package com.example.gymerp.dto;

import org.apache.ibatis.type.Alias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Alias("VoucherLogDto")
@Builder 
@NoArgsConstructor 
@AllArgsConstructor
@Data
public class VoucherLogDto {
	private Long voucherId;     // 이용권 고유번호 (PK)
    private Long memNum;        // 회원 번호 (FK)
    private String memberName;  // 회원 이름
    private String startDate;   // 이용권 시작일
    private String endDate;     // 이용권 종료일
}
