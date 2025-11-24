package com.example.gymerp.dto;


import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockAdjustRequestDto {

	@NotNull
	private String action;   // "ADD" or "SUBTRACT"
	@Min(1)
    private int quantity; // 수량
    @Size(max = 200)
    private String notes;    // 사유 or 비고
    

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime date;
}