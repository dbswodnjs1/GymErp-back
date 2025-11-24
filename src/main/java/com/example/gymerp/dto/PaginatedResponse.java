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
public class PaginatedResponse<T> {

	private List<T> list;
	
	private int totalPageCount;
	private int pageNum;
}
