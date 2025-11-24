package com.example.gymerp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

	private int productId;
	private String codeAId;
	private String codeBId;
	private String codeBName;
	private String name;
	private BigDecimal price;
	private Boolean isActive;
	private String note;
	private int quantity; // 입고 내역 테이블에 수량으로 저장할 필드
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	//페이징 처리를 위한 필드
	private int startRowNum;
	private int endRowNum;
	//프로필 이미지 출력을 위한 필드
	private String profileImage;
	//검색 키워드를 담기 위한 필드
	private String keyword;
	//검색 조건을 담기 위한 필드
	private List<String> categoryCodes;
	
	// <input type="file" name="profileFile" > 을 처리하기 위한 필드
	private MultipartFile profileFile;
	
	//정렬용 필드
	private String sortBy;    // (정렬 기준 컬럼: "name", "price" 등)
	private String direction; // (정렬 방향: "ASC", "DESC")
}
