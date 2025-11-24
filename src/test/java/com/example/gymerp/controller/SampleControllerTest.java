package com.example.gymerp.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 *  [Controller 테스트 목적]
 * - API 엔드포인트가 정상적으로 작동하는지 확인.
 * - 실제 서버 띄우지 않고 MockMvc로 요청 시뮬레이션.
 * - @AutoConfigureMockMvc: MockMvc 객체 자동 구성.
 * - addFilters=false → Spring Security 같은 필터를 비활성화하여 API 테스트만 집중.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc; // HTTP 요청 흉내 내는 객체

    @Test
    @DisplayName("상품 목록 API 테스트")
    void getProductList() throws Exception {
        // when & then: GET /v1/products 요청 후 상태 및 응답 구조 확인
        mockMvc.perform(get("/v1/products"))
                .andExpect(status().isOk())                  // HTTP 200 OK
                .andExpect(jsonPath("$[0].name").exists())   // 첫 상품 name 필드 존재
                .andExpect(jsonPath("$[0].price").isNumber());// 가격이 숫자인지
    }
}
