package com.example.gymerp.repository;

import com.example.gymerp.dto.DashboardKpiDto;

public interface DashboardDao {
	DashboardKpiDto selectKpis();
}
