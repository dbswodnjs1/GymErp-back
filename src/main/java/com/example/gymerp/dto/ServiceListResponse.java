package com.example.gymerp.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ServiceListResponse {

	private List<ServiceDto> list;
	private int startPageNum;
	private int endPageNum;
	private int totalPageCount;
	private int pageNum;
	private int totalRow;
	private String keyword; //검색 키워드
	//검색 조건을 담기 위한 필드
	private List<String> categoryCodes;
	private String query; //GET 방식 파라미터 "search=xxx&keyword=xxx" or ""	
}
