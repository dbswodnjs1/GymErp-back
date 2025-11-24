// src/main/java/com/example/gymerp/controller/DashboardController.java
package com.example.gymerp.controller;

import com.example.gymerp.dto.DashboardKpiDto;
import com.example.gymerp.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/home")
    public DashboardKpiDto getKpis() {
        return dashboardService.getKpis();
    }
}
