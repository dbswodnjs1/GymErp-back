package com.example.gymerp.service;

import java.util.List;
import java.util.Map;

import com.example.gymerp.dto.SalesItemDto;



public interface SalesItemService {

    // 상품 판매 목록 전체 조회 및 검색 (페이징 포함)
	public Map<String, Object> getAllSalesItems(String startDate, String endDate, String productNameKeyword, Integer empNum, int page, int size);

    // 상품 판매 단건 조회
    SalesItemDto getSalesItemById(Long itemSalesId);

    // 상품 판매 등록
    int addSalesItem(SalesItemDto salesItem);
    
    // 상품 판매 수정
    int updateSalesItem(SalesItemDto salesItem);

    // 상품 판매 삭제
    int deleteSalesItem(Long itemSalesId);

    // 상품 판매 분석 및 통계 조회
	List<Map<String, Object>> getItemSalesAnalytics(String startDate, String endDate, List<Integer> itemIds, Integer memNum,
			Integer empNum);
	
}