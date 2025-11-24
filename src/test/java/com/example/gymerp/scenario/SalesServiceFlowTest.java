package com.example.gymerp.scenario;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * [SalesServiceFlowTest]
 * - 서비스(회원권 or PT) 판매 등록 → 수정(부분환불) → 삭제(전체환불 및 로그 반영) 시나리오
 * - 실제 Controller, Service, DAO, Mapper 전체 연동
 * - 트랜잭션 롤백으로 테스트 후 DB 영향 없음
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
class SalesServiceFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("서비스 판매 등록 → 수정(부분환불) → 삭제(전체환불) 시나리오 테스트")
    void salesServiceScenario() throws Exception {

        // 1️. 서비스 판매 등록 (회원권 예시)
        mockMvc.perform(post("/v1/sales/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "serviceName": "회원권 1개월",
                                "serviceType": "VOUCHER",
                                "memNum": 100,
                                "empNum": 200,
                                "actualCount": 30,
                                "actualAmount": 100000
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("판매 내역이 등록되었습니다."))
                .andExpect(jsonPath("$.result").value(1));

        // 2️. 서비스 판매 수정 (부분환불 처리)
        mockMvc.perform(put("/v1/sales/services/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "serviceSalesId": 1,
                                "actualCount": 20,
                                "actualAmount": 80000,
                                "serviceType": "VOUCHER"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("판매 내역이 수정되었습니다."))
                .andExpect(jsonPath("$.result").value(1));

        // 3️. 서비스 판매 삭제 (전체환불 및 로그/롤백 처리)
        mockMvc.perform(delete("/v1/sales/services/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("판매 내역이 삭제되었습니다."))
                .andExpect(jsonPath("$.result").value(1));

        // 4️. 판매 내역 조회 (삭제 후 목록 조회)
        mockMvc.perform(get("/v1/sales/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
