package com.example.gymerp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.gymerp.dto.CodeDto;
import com.example.gymerp.repository.CategoryDao;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{
	
	private final CategoryDao categoryDao;
	
	@Override
	public List<CodeDto> getAllCodes(CodeDto dto) {
		
		return categoryDao.getCodeList(dto);
	}

	@Override
	public void save(CodeDto dto) {
		categoryDao.insert(dto);
		
	}
	
	// 일정목록
	@Override
	public List<CodeDto> getScheduleCodes() {
	   return categoryDao.getScheduleCodes(); 
	}
}
