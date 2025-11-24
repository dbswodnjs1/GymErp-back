package com.example.gymerp.service;

import java.util.List;

import com.example.gymerp.dto.CurrentStockDto;
import com.example.gymerp.dto.PaginatedResponse;
import com.example.gymerp.dto.PurchaseDto;
import com.example.gymerp.dto.StockAdjustRequestDto;
import com.example.gymerp.dto.StockAdjustmentDto;

public interface StockService {
	int getStockOne(int productId);
	Boolean isStockSufficient(int productId, int quantity);
	PaginatedResponse<PurchaseDto> getProductInboundDetail(int productId, int inboundPage, int size, String startDate, String endDate); // 2-1
    PaginatedResponse<StockAdjustmentDto> getProductOutboundDetail(int productId, int outboundPage, int size, String startDate, String endDate); // 2-2
    List<CurrentStockDto> getProductStockList(int page, int size, String keyword); // 2-3
    void adjustProduct(int productId, StockAdjustRequestDto request); // 3-*
}
