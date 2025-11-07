// src/main/java/com/example/gymerp/service/impl/DashboardServiceImpl.java
package com.example.gymerp.service;

import com.example.gymerp.repository.DashboardDao;
import com.example.gymerp.dto.DashboardKpiDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final DashboardDao dashboardDao;

    @Override
    public DashboardKpiDto getKpis() {
        return dashboardDao.selectKpis();
    }
}
