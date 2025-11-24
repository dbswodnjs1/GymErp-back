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
 * [PTFlowTest]
 * - PT 등록 → 예약(소비) → 예약취소(복구) → 남은 횟수 확인까지의 전체 흐름 검증
 * - Controller + Service + DAO + Mapper 전체 통합
 * - 트랜잭션 롤백으로 DB 영향 없이 테스트 가능
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
class PtFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("PT 충전 → 소비 → 예약취소 → 남은 횟수 검증")
    void ptReservationFlow() throws Exception {

        // 1️. PT 충전 등록
        mockMvc.perform(post("/v1/log/pt/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "memNum": 100,
                                "empNum": 200,
                                "status": "충전",
                                "countChange": 10,
                                "salesId": 1
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("PT 충전 로그가 등록되었습니다."));

        // 2️. PT 소비 로그 등록 (예약)
        mockMvc.perform(post("/v1/log/pt/consume")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "memNum": 100,
                                "empNum": 200,
                                "status": "소비",
                                "regId": 999
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("PT 소비 로그가 등록되었습니다."));

        // 3️. PT 예약 취소 로그 등록 (복구)
        mockMvc.perform(post("/v1/log/pt/change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "memNum": 100,
                                "empNum": 200,
                                "status": "예약취소",
                                "regId": 999
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("PT 트레이너 변경 로그가 등록되었습니다."));

        // 4️. 남은 PT 횟수 확인
        mockMvc.perform(get("/v1/log/pt/100/remaining"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memNum").value(100))
                .andExpect(jsonPath("$.remainingCount").isNumber());
    }
}
