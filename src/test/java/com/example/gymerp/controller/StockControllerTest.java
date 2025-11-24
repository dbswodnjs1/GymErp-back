package com.example.gymerp.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.mybatis.spring.boot.autoconfigure.MybatisLanguageDriverAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.gymerp.dto.CurrentStockDto;
import com.example.gymerp.dto.PaginatedResponse;
import com.example.gymerp.dto.PurchaseDto;
import com.example.gymerp.dto.StockAdjustRequestDto;
import com.example.gymerp.dto.StockAdjustmentDto;
import com.example.gymerp.service.StockService;
import com.example.gymerp.support.ControllerSliceTestBase;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * StockController 슬라이스 테스트
 * - 컨트롤러만 로드, 서비스는 Mock, DAO/MyBatis는 완전 배제
 * - 기능 번호 매칭:
 *   1-1: GET /v1/stock/{productId} (개별 가용재고)
 *   2-1: GET /v1/stock/{productId}/inbound
 *   2-2: GET /v1/stock/{productId}/outbound
 *   2-3: GET /v1/stock (페이지 목록)
 *   3  : POST /v1/stock/{productId}/adjust
 */
@WebMvcTest(
    controllers = StockController.class,
    excludeFilters = {
        // ✅ 슬라이스에서 Repository/Mapper 빈 제외 (MyBatis/DAO 차단)
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class),
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Mapper.class)
    },
    excludeAutoConfiguration = {
        // ✅ MyBatis 자동구성 제거
        MybatisAutoConfiguration.class,
        MybatisLanguageDriverAutoConfiguration.class
    }
)
@AutoConfigureMockMvc(addFilters = false) // 보안필터 제외
class StockControllerTest extends ControllerSliceTestBase {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    // 컨트롤러가 위임하는 서비스만 Mock 처리
    @MockitoBean StockService stockService;


    // --------------------------------------------------------------------
    // 2-3. GET /v1/stock - 기본 파라미터(page=1,size=20,keyword=null) 위임 및 배열 응답 검증
    // --------------------------------------------------------------------
    @Test
    @DisplayName("2-3) GET /v1/stock - 기본 파라미터로 전체 재고 목록 조회")
    void list_defaultParams_ok() throws Exception {
        List<CurrentStockDto> items = List.of(
            CurrentStockDto.builder()
                .productId(1).productName("단백질 보충제")
                .totalInbound(10).totalOutbound(1).totalSales(2).currentStock(7)
                .build(),
            CurrentStockDto.builder()
                .productId(2).productName("요가매트")
                .totalInbound(5).totalOutbound(0).totalSales(0).currentStock(5)
                .build()
        );
        when(stockService.getProductStockList(1, 20, null)).thenReturn(items);

        mockMvc.perform(get("/v1/stock").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].productId", is(1)))
            .andExpect(jsonPath("$[0].productName", is("단백질 보충제")))
            .andExpect(jsonPath("$[0].totalInbound", is(10)))
            .andExpect(jsonPath("$[0].totalOutbound", is(1)))
            .andExpect(jsonPath("$[0].totalSales", is(2)))
            .andExpect(jsonPath("$[0].currentStock", is(7)))
            .andExpect(jsonPath("$[1].productId", is(2)))
            .andExpect(jsonPath("$[1].currentStock", is(5)));

        // 서비스 호출 파라미터 검증
        verify(stockService).getProductStockList(1, 20, null);
    }

    // --------------------------------------------------------------------
    // 2-3. GET /v1/stock - 쿼리파라미터(page/size/keyword) 전달 검증
    // --------------------------------------------------------------------
    @Test
    @DisplayName("2-3) GET /v1/stock - page/size/keyword 전달 검증")
    void list_withKeyword_ok() throws Exception {
        String keyword = "요가";
        when(stockService.getProductStockList(2, 10, keyword)).thenReturn(List.of());

        mockMvc.perform(get("/v1/stock")
                .param("page", "2")
                .param("size", "10")
                .param("keyword", keyword)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(stockService).getProductStockList(2, 10, keyword);
    }

    // --------------------------------------------------------------------
    // 2-1. GET /v1/stock/{productId}/inbound - 특정 상품 입고 이력
    // --------------------------------------------------------------------
    @Test
    @DisplayName("2-1) GET /v1/stock/{productId}/inbound - 입고 내역 페이지 반환")
    void inbound_ok() throws Exception {
        int productId = 1;
        List<PurchaseDto> inboundList = List.of(
            PurchaseDto.builder()
                .purchaseId(101).productId(productId).codeBId("SUPPLEMENT")
                .createdAt(LocalDateTime.of(2025, 10, 1, 9, 0))
                .quantity(3).notes("첫 입고").build(),
            PurchaseDto.builder()
                .purchaseId(102).productId(productId).codeBId("EQUIP")
                .createdAt(LocalDateTime.of(2025, 10, 10, 10, 30))
                .quantity(5).notes("추가 입고").build()
        );
        PaginatedResponse<PurchaseDto> response = PaginatedResponse.<PurchaseDto>builder()
                .list(inboundList)
                .pageNum(1)
                .totalPageCount(3)
                .build();
        when(stockService.getProductInboundDetail(productId, 1, 20, null, null)).thenReturn(response);

        mockMvc.perform(get("/v1/stock/{productId}/inbound", productId)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pageNum", is(1)))
            .andExpect(jsonPath("$.totalPageCount", is(3)))
            .andExpect(jsonPath("$.list", hasSize(2)))
            .andExpect(jsonPath("$.list[0].purchaseId", is(101)))
            .andExpect(jsonPath("$.list[0].quantity", is(3)))
            .andExpect(jsonPath("$.list[0].notes", is("첫 입고")));

        verify(stockService).getProductInboundDetail(productId, 1, 20, null, null);
    }

    // --------------------------------------------------------------------
    // 2-2. GET /v1/stock/{productId}/outbound - 특정 상품 출고/판매 이력
    // --------------------------------------------------------------------
    @Test
    @DisplayName("2-2) GET /v1/stock/{productId}/outbound - 출고+판매 내역 페이지 반환")
    void outbound_ok() throws Exception {
        int productId = 1;
        List<StockAdjustmentDto> outboundList = List.of(
            StockAdjustmentDto.builder()
                .adjustmentId(201).productId(productId).codeBId("SUPPLEMENT")
                .createdAt(LocalDateTime.of(2025, 10, 15, 14, 0))
                .quantity(2).notes("파손 처리").build(),
            StockAdjustmentDto.builder()
                .adjustmentId(202).productId(productId).codeBId("EQUIP")
                .createdAt(LocalDateTime.of(2025, 10, 20, 16, 30))
                .quantity(1).notes("지점 이동").build()
        );
        PaginatedResponse<StockAdjustmentDto> response = PaginatedResponse.<StockAdjustmentDto>builder()
                .list(outboundList)
                .pageNum(1)
                .totalPageCount(2)
                .build();
        when(stockService.getProductOutboundDetail(productId, 1, 20, null, null)).thenReturn(response);

        mockMvc.perform(get("/v1/stock/{productId}/outbound", productId)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pageNum", is(1)))
            .andExpect(jsonPath("$.totalPageCount", is(2)))
            .andExpect(jsonPath("$.list", hasSize(2)))
            .andExpect(jsonPath("$.list[0].adjustmentId", is(201)))
            .andExpect(jsonPath("$.list[0].quantity", is(2)))
            .andExpect(jsonPath("$.list[0].notes", is("파손 처리")));

        verify(stockService).getProductOutboundDetail(productId, 1, 20, null, null);
    }

    // --------------------------------------------------------------------
    // 1-1. GET /v1/stock/{productId} - 개별 상품 가용재고
    // --------------------------------------------------------------------
    @Test
    @DisplayName("1-1) GET /v1/stock/{productId} - 개별 가용 재고 조회")
    void getStockOne_ok() throws Exception {
        int productId = 7;
        when(stockService.getStockOne(productId)).thenReturn(42);

        mockMvc.perform(get("/v1/stock/{productId}", productId)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("42"));
    }

    // --------------------------------------------------------------------
    // 3. POST /v1/stock/{productId}/adjust - 정상: 204(No Content)
    // --------------------------------------------------------------------
    @Test
    @DisplayName("3) POST /v1/stock/{productId}/adjust - 정상 요청 시 204")
    void adjust_ok_returns204() throws Exception {
        int productId = 1;
        StockAdjustRequestDto addRequest = StockAdjustRequestDto.builder()
            .action("ADD").quantity(3).notes("테스트 입고").build();

        // void 메서드는 굳이 stub 불필요 (예외만 안 던지면 됨)
        mockMvc.perform(post("/v1/stock/{productId}/adjust", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRequest)))
            .andExpect(status().isNoContent());
    }

    // --------------------------------------------------------------------
    // 3. POST /v1/stock/{productId}/adjust - 잘못된 요청: 400(Bad Request)
    // --------------------------------------------------------------------
    @Test
    @DisplayName("3) POST /v1/stock/{productId}/adjust - 0 이하 수량이면 400")
    void adjust_invalidQty_returns400() throws Exception {
        int productId = 1;
        StockAdjustRequestDto badRequest = StockAdjustRequestDto.builder()
            .action("SUBTRACT").quantity(0).notes("테스트 오류").build();

        doThrow(new IllegalArgumentException("수량은 1 이상이어야 합니다."))
            .when(stockService)
            .adjustProduct(anyInt(), any());

        mockMvc.perform(post("/v1/stock/{productId}/adjust", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badRequest)))
            .andExpect(status().isBadRequest());
    }
}
