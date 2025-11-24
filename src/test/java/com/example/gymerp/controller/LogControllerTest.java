package com.example.gymerp.controller;

import com.example.gymerp.dto.PtLogDto;
import com.example.gymerp.dto.VoucherLogDto;
import com.example.gymerp.service.LogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LogController.class)
@AutoConfigureMockMvc(addFilters = false)
class LogControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean LogService logService;

    /* ================================
       [회원권 관련 테스트]
    ================================ */

    @Test
    @DisplayName("GET /v1/log/voucher/check - 회원권 유효 여부 확인")
    void checkVoucherValid() throws Exception {
        Long memNum = 1L;
        when(logService.isVoucherValid(memNum)).thenReturn(true);

        mockMvc.perform(get("/v1/log/voucher/check")
                        .param("memNum", String.valueOf(memNum))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memNum").value(memNum))
                .andExpect(jsonPath("$.isValid").value(true));
    }

    @Test
    @DisplayName("GET /v1/log/voucher/{memNum} - 회원권 단건 조회")
    void getVoucherByMember() throws Exception {
        Long memNum = 1L;
        VoucherLogDto mockVoucher = VoucherLogDto.builder()
                .voucherId(100L)
                .memNum(memNum)
                .memberName("홍길동")
                .startDate("2025-01-01")
                .endDate("2025-02-01")
                .build();

        when(logService.getVoucherByMember(memNum)).thenReturn(mockVoucher);

        mockMvc.perform(get("/v1/log/voucher/{memNum}", memNum)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.voucher.memNum").value(memNum))
                .andExpect(jsonPath("$.voucher.memberName").value("홍길동"))
                .andExpect(jsonPath("$.voucher.startDate").value("2025-01-01"));
    }

    /* ================================
       [PT 관련 테스트]
    ================================ */

    @Test
    @DisplayName("GET /v1/log/pt/{memNum}/remaining - 남은 PT 횟수 조회")
    void getRemainingPtCount() throws Exception {
        Long memNum = 1L;
        when(logService.getRemainingPtCount(memNum)).thenReturn(7);

        mockMvc.perform(get("/v1/log/pt/{memNum}/remaining", memNum)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memNum").value(memNum))
                .andExpect(jsonPath("$.remainingCount").value(7));
    }

    @Test
    @DisplayName("GET /v1/log/pt/refund/{refundId} - 특정 환불 로그 단건 조회")
    void getRefundLog() throws Exception {
        Long refundId = 10L;
        PtLogDto mockLog = PtLogDto.builder()
                .usageId(refundId)
                .memNum(1L)
                .empNum(2L)
                .status("부분환불")
                .countChange(-2L)
                .build();

        when(logService.getPtLogByRefundId(refundId)).thenReturn(mockLog);

        mockMvc.perform(get("/v1/log/pt/refund/{refundId}", refundId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.refundLog.usageId").value(refundId))
                .andExpect(jsonPath("$.refundLog.status").value("부분환불"))
                .andExpect(jsonPath("$.refundLog.countChange").value(-2));
    }
}
