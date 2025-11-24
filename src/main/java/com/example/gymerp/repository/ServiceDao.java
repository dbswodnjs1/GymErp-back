package com.example.gymerp.repository;

import java.util.List;

import com.example.gymerp.dto.ServiceDto;

public interface ServiceDao {

	public List<ServiceDto> selectPage(ServiceDto dto);
	public int getCount(ServiceDto dto); 
	public void insert(ServiceDto dto);
	public int update(ServiceDto dto);
	public ServiceDto getByNum(int serviceId);
	public int updateServiceStatus(ServiceDto dto);
}
