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
 * [VoucherFlowTest]
 * - 회원권 등록 → 연장 → 부분환불 → 전체환불 → 유효성 검증 시나리오 통합 테스트
 * - Controller + Service + DAO + Mapper 전체 연동 검증
 * - 실제 DB 트랜잭션 기반, 테스트 후 자동 롤백 처리
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
class VoucherFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("회원권 등록 → 연장 → 부분환불 → 전체환불 → 유효여부 확인 시나리오")
    void voucherLifecycleScenario() throws Exception {

        // 1️. 회원권 신규 등록
        mockMvc.perform(post("/v1/log/voucher")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "memNum": 100,
                                "memberName": "김회원",
                                "startDate": "2025-11-01",
                                "endDate": "2025-11-30"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원권이 등록되었습니다."));

        // 2️. 회원권 기간 연장 (예: 30일 → 60일)
        mockMvc.perform(put("/v1/log/voucher/extend/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "memNum": 100,
                                "memberName": "김회원",
                                "endDate": "2025-12-30"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원권이 연장되었습니다."));

        // 3️. 회원권 부분환불 (기간 단축)
        mockMvc.perform(put("/v1/log/voucher/refund/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "memNum": 100,
                                "memberName": "김회원",
                                "endDate": "2025-12-10"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원권이 부분환불되었습니다."));

        // 4️. 회원권 전체환불 (회귀)
        mockMvc.perform(put("/v1/log/voucher/rollback/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "memNum": 100,
                                "memberName": "김회원",
                                "endDate": "2025-11-01"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원권이 전체환불(회귀) 처리되었습니다."));

        // 5️. 회원권 유효 여부 확인
        mockMvc.perform(get("/v1/log/voucher/check")
                        .param("memNum", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memNum").value(100))
                .andExpect(jsonPath("$.isValid").isBoolean());
    }
}
