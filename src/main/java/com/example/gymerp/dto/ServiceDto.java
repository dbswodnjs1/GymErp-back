package com.example.gymerp.dto;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.type.Alias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Alias("ServiceDto")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDto {
	
	private int serviceId;
	private String codeAId;
	private String codeBId;
	private String codeBName;
	private String name;
	private BigDecimal price;
	private Boolean isActive;
	private String note;
	private int serviceValue;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	private int prevNum;
	
	//페이징 처리를 위한 필드
	private int startRowNum;
	private int endRowNum;
	//검색 키워드를 담기 위한 필드
	private String keyword;
	//검색 조건을 담기 위한 필드
	private List<String> categoryCodes;
	
	//정렬용 필드
	private String sortBy;    // (정렬 기준 컬럼: "name", "price" 등)
	private String direction; // (정렬 방향: "ASC", "DESC")
}
