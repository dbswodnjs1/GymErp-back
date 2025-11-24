package com.example.gymerp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeDto {

	private String codeAId;
	private String codeBId;
	private String codeAName;
	private String codeBName;
	
}
