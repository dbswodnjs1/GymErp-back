package com.example.gymerp.service;

import com.example.gymerp.dto.ServiceDto;
import com.example.gymerp.dto.ServiceListResponse;

public interface ServiceService {

	public ServiceListResponse getServices(int pageNum, ServiceDto dto, String sortBy, String direction);
	public void save(ServiceDto dto);
	public void modifyService(ServiceDto dto);
	public ServiceDto getDetail(int serviceId);
	public void updateServiceStatus(int serviceId, boolean isActive);
}
