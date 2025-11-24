package com.example.gymerp.scenario;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 *  [Scenario 테스트 목적]
 * - 실제 업무 흐름(상품 재고 조정 → 판매 등록 → 매출 확인)을 통합 검증.
 * - Service + Controller + Repository 전부 연동됨.
 * - 통합 시나리오가 제대로 작동하는지 한 번에 확인 가능.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
class SalesFlowTest {

    @Autowired
    private MockMvc mockMvc; // 실제 요청 시뮬레이션용 객체

    @Test
    @DisplayName("상품 재고 추가 후 매출 등록 시 정상 작동 테스트")
    void testSalesScenario() throws Exception {

        //️  1. 상품 목록 조회
        mockMvc.perform(get("/v1/products"))
                .andExpect(status().isOk());

        // 2. 상품 재고 추가 (입고 처리 시뮬레이션)
        mockMvc.perform(post("/v1/stock/1/increase")
                .param("quantity", "10"))
                .andExpect(status().isOk());

        // 3. 상품 매출 등록 (JSON 바디 전송)
        mockMvc.perform(post("/v1/sales/items")
                .contentType("application/json")
                .content("""
                    {
                        "productId": 1,
                        "empNum": 1,
                        "productName": "단백질 보충제",
                        "quantity": 2,
                        "unitPrice": 30000,
                        "totalAmount": 60000,
                        "productType": "PRODUCT"
                    }
                """))
                .andExpect(status().isOk());

        // 4. 매출 내역 확인
        mockMvc.perform(get("/v1/sales/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productName").value("단백질 보충제"));
    }
}
