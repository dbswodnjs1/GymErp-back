package com.example.gymerp.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.example.gymerp.dto.CurrentStockDto;
import com.example.gymerp.dto.PurchaseDto;
import com.example.gymerp.dto.StockAdjustmentDto;

public interface StockDao {
	// 1-1 가용 재고 조회
	int getAvailableQty(int productId);
	
	// 2-1 입고 내역 조회
    List<PurchaseDto> getPurchaseList(Map<String, Object> params);
    
    // 2-1-1 입고내역 row 수 계산
    int getPurchaseListCount(Map<String, Object> params);

    // 2-2 출고 + 판매 내역 조회
    List<StockAdjustmentDto> getAdjustStockAndSalesList(Map<String, Object> params);

    // 2-2-1 출고 + 판매 내역 row 수 계산
    int getAdjustStockAndSalesListCount(Map<String, Object> params);

    // 2-3 현재 재고 현황 조회
    List<CurrentStockDto> getCurrentStockListPaged(int offset, int size, String keyword);

    // 3-1 입고 등록
    int insertPurchase(PurchaseDto dto);

    // 3-2 출고 등록
    int insertStockAdjustment(StockAdjustmentDto dto);
    
}
