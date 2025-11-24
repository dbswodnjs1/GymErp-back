package com.example.gymerp.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentStockDto {
	private int productId;
	private String productName;
	private int totalInbound;
	private int totalOutbound;
	private int totalSales;
	private int currentStock;
}
