package com.example.gymerp.controller;

import com.example.gymerp.dto.SalesService;
import com.example.gymerp.service.SalesServiceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * [SalesServiceController 단위 테스트]
 * - @WebMvcTest 기반 (Controller만 로드)
 * - Service 계층은 Mock 처리
 * - 보안 필터 비활성화 상태에서 API 구조 검증
 */
@WebMvcTest(controllers = SalesServiceController.class)
@AutoConfigureMockMvc(addFilters = false)
class SalesServiceControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean SalesServiceService salesServiceService;

    /* ===============================
       [1. 조회]
    =============================== */

    @Test
    @DisplayName("GET /v1/sales/services - 전체 판매 내역 조회")
    void getAllSalesServices() throws Exception {
        List<SalesService> list = List.of(
                SalesService.builder()
                        .serviceSalesId(1L)
                        .serviceName("PT 10회권")
                        .empNum(2L)
                        .memNum(3L)
                        .actualCount(10)
                        .actualAmount(500000L)
                        .serviceType("PT")
                        .status("ACTIVE")
                        .build(),
                SalesService.builder()
                        .serviceSalesId(2L)
                        .serviceName("회원권 1개월")
                        .empNum(2L)
                        .memNum(4L)
                        .actualCount(30)
                        .actualAmount(100000L)
                        .serviceType("VOUCHER")
                        .status("ACTIVE")
                        .build()
        );

        when(salesServiceService.getAllSalesServices()).thenReturn(list);

        mockMvc.perform(get("/v1/sales/services")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].serviceSalesId").value(1))
                .andExpect(jsonPath("$[0].serviceName").value("PT 10회권"))
                .andExpect(jsonPath("$[1].serviceSalesId").value(2))
                .andExpect(jsonPath("$[1].serviceName").value("회원권 1개월"));
    }

    @Test
    @DisplayName("GET /v1/sales/services/{id} - 단일 판매 내역 조회")
    void getSalesServiceById() throws Exception {
        Long id = 1L;
        SalesService mockService = SalesService.builder()
                .serviceSalesId(id)
                .serviceName("PT 10회권")
                .empNum(2L)
                .memNum(3L)
                .actualCount(10)
                .actualAmount(500000L)
                .serviceType("PT")
                .build();

        when(salesServiceService.getSalesServiceById(id)).thenReturn(mockService);

        mockMvc.perform(get("/v1/sales/services/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceSalesId").value(id))
                .andExpect(jsonPath("$.serviceName").value("PT 10회권"))
                .andExpect(jsonPath("$.serviceType").value("PT"));
    }

    /* ===============================
       [2. 등록]
    =============================== */

    @Test
    @DisplayName("POST /v1/sales/services - 판매 등록")
    void createSalesService() throws Exception {
        SalesService dto = SalesService.builder()
                .serviceName("회원권 1개월")
                .empNum(2L)
                .memNum(3L)
                .actualCount(30)
                .actualAmount(100000L)
                .serviceType("VOUCHER")
                .build();

        when(salesServiceService.createSalesService(any(SalesService.class))).thenReturn(1);

        mockMvc.perform(post("/v1/sales/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(1))
                .andExpect(jsonPath("$.message").value("판매 내역이 등록되었습니다."));
    }

    /* ===============================
       [3. 수정]
    =============================== */

    @Test
    @DisplayName("PUT /v1/sales/services/{id} - 판매 수정")
    void updateSalesService() throws Exception {
        Long id = 1L;
        SalesService dto = SalesService.builder()
                .serviceSalesId(id)
                .actualCount(8)
                .actualAmount(400000L)
                .serviceType("PT")
                .build();

        when(salesServiceService.updateSalesService(any(SalesService.class))).thenReturn(1);

        mockMvc.perform(put("/v1/sales/services/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(1))
                .andExpect(jsonPath("$.message").value("판매 내역이 수정되었습니다."));
    }

    /* ===============================
       [4. 삭제]
    =============================== */

    @Test
    @DisplayName("DELETE /v1/sales/services/{id} - 판매 삭제")
    void deleteSalesService() throws Exception {
        Long id = 1L;
        when(salesServiceService.deleteSalesService(eq(id))).thenReturn(1);

        mockMvc.perform(delete("/v1/sales/services/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(1))
                .andExpect(jsonPath("$.message").value("판매 내역이 삭제되었습니다."));
    }
}
