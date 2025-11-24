package com.example.gymerp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.gymerp.dto.CurrentStockDto;
import com.example.gymerp.dto.PaginatedResponse;
import com.example.gymerp.dto.ProductDto;
import com.example.gymerp.dto.PurchaseDto;
import com.example.gymerp.dto.StockAdjustRequestDto;
import com.example.gymerp.dto.StockAdjustmentDto;
import com.example.gymerp.repository.ProductDao;
import com.example.gymerp.repository.StockDao;

@ExtendWith(MockitoExtension.class)
class StockServiceImplTest {

    @Mock private StockDao stockDao;
    @Mock private ProductDao productDao;

    @InjectMocks
    private StockServiceImpl stockService;

    @Test
    @DisplayName("getProductStockList는 페이지와 사이즈를 보정해 DAO에 위임한다")
    void getProductStockList_normalizesArguments() {
        when(stockDao.getCurrentStockListPaged(0, 100, null)).thenReturn(List.of());

        List<CurrentStockDto> result = stockService.getProductStockList(0, 200, null);

        assertThat(result).isEmpty();
        verify(stockDao).getCurrentStockListPaged(0, 100, null);
        verifyNoInteractions(productDao);
    }

    @Test
    @DisplayName("getProductInboundDetail은 productId가 0 이하면 예외를 던진다")
    void getProductInboundDetail_invalidProductId() {
        assertThatThrownBy(() -> stockService.getProductInboundDetail(0, 1, 20, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 productId");

        verifyNoInteractions(stockDao, productDao);
    }

    @Test
    @DisplayName("getProductInboundDetail은 페이지 메타와 필터를 Map으로 DAO에 전달한다")
    void getProductInboundDetail_buildsResponseWithMetadata() {
        int productId = 7;
        int page = 2;
        int size = 15;
        String startDate = "2025-01-01";
        String endDate = "2025-01-31";

        List<PurchaseDto> purchases = List.of(
                PurchaseDto.builder()
                        .purchaseId(101)
                        .productId(productId)
                        .quantity(3)
                        .build()
        );

        when(stockDao.getPurchaseListCount(anyMap())).thenReturn(23);
        when(stockDao.getPurchaseList(anyMap())).thenReturn(purchases);

        PaginatedResponse<PurchaseDto> response =
                stockService.getProductInboundDetail(productId, page, size, startDate, endDate);

        assertThat(response.getList()).containsExactlyElementsOf(purchases);
        assertThat(response.getPageNum()).isEqualTo(page);
        assertThat(response.getTotalPageCount()).isEqualTo(2);

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(stockDao).getPurchaseListCount(captor.capture());
        Map<String, Object> params = captor.getValue();
        assertThat(params)
                .containsEntry("productId", productId)
                .containsEntry("offset", 15)
                .containsEntry("size", size)
                .containsEntry("startDate", startDate)
                .containsEntry("endDate", endDate);

        verify(stockDao).getPurchaseList(params);
        verifyNoMoreInteractions(stockDao);
        verifyNoInteractions(productDao);
    }

    @Test
    @DisplayName("getProductOutboundDetail은 페이지 메타와 필터를 Map으로 DAO에 전달한다")
    void getProductOutboundDetail_buildsResponseWithMetadata() {
        int productId = 3;
        int page = 3;
        int size = 10;
        String startDate = "2025-02-01";
        String endDate = "2025-02-28";

        StockAdjustmentDto dto = StockAdjustmentDto.builder()
                .adjustmentId(201)
                .productId(productId)
                .quantity(4)
                .build();

        when(stockDao.getAdjustStockAndSalesListCount(anyMap())).thenReturn(30);
        when(stockDao.getAdjustStockAndSalesList(anyMap())).thenReturn(List.of(dto));

        PaginatedResponse<StockAdjustmentDto> response =
                stockService.getProductOutboundDetail(productId, page, size, startDate, endDate);

        assertThat(response.getList()).containsExactly(dto);
        assertThat(response.getPageNum()).isEqualTo(page);
        assertThat(response.getTotalPageCount()).isEqualTo(3);

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(stockDao).getAdjustStockAndSalesListCount(captor.capture());
        Map<String, Object> params = captor.getValue();
        assertThat(params)
                .containsEntry("productId", productId)
                .containsEntry("offset", 20)
                .containsEntry("size", size)
                .containsEntry("startDate", startDate)
                .containsEntry("endDate", endDate);

        verify(stockDao).getAdjustStockAndSalesList(params);
        verifyNoMoreInteractions(stockDao);
        verifyNoInteractions(productDao);
    }

    @Nested
    @DisplayName("adjustProduct")
    class AdjustProduct {

        @Test
        @DisplayName("수량이 0 이하이면 예외")
        void invalidQuantity() {
            var request = StockAdjustRequestDto.builder()
                    .action("ADD")
                    .quantity(0)
                    .build();

            assertThatThrownBy(() -> stockService.adjustProduct(1, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("수량은 1 이상");

            verifyNoInteractions(stockDao, productDao);
        }

        @Test
        @DisplayName("상품이 존재하지 않으면 예외")
        void productMissing() {
            var request = StockAdjustRequestDto.builder()
                    .action("ADD")
                    .quantity(2)
                    .build();

            when(productDao.getByNum(10)).thenReturn(null);

            assertThatThrownBy(() -> stockService.adjustProduct(10, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유효하지 않은 상품");

            verify(productDao).getByNum(10);
            verifyNoMoreInteractions(productDao);
            verifyNoInteractions(stockDao);
        }

        @Test
        @DisplayName("ADD 요청은 Purchase insert 를 호출한다")
        void addAction_insertsPurchase() {
            int productId = 5;
            LocalDateTime requestedAt = LocalDateTime.of(2025, 3, 1, 10, 30);

            when(productDao.getByNum(productId)).thenReturn(
                    ProductDto.builder().productId(productId).codeBId("SUPPLEMENT").build()
            );
            when(stockDao.insertPurchase(any(PurchaseDto.class))).thenReturn(1);

            var request = StockAdjustRequestDto.builder()
                    .action("ADD")
                    .quantity(4)
                    .notes("테스트 입고")
                    .date(requestedAt)
                    .build();

            stockService.adjustProduct(productId, request);

            ArgumentCaptor<PurchaseDto> captor = ArgumentCaptor.forClass(PurchaseDto.class);
            verify(stockDao).insertPurchase(captor.capture());
            PurchaseDto dto = captor.getValue();
            assertThat(dto.getProductId()).isEqualTo(productId);
            assertThat(dto.getCodeBId()).isEqualTo("SUPPLEMENT");
            assertThat(dto.getQuantity()).isEqualTo(4);
            assertThat(dto.getNotes()).isEqualTo("테스트 입고");
            assertThat(dto.getCreatedAt()).isEqualTo(requestedAt);

            verifyNoMoreInteractions(stockDao);
        }

        @Test
        @DisplayName("SUBTRACT 요청은 재고 부족 시 예외")
        void subtractAction_insufficientStock() {
            int productId = 2;
            when(productDao.getByNum(productId)).thenReturn(
                    ProductDto.builder().productId(productId).codeBId("SUPPLEMENT").build()
            );
            when(stockDao.getAvailableQty(productId)).thenReturn(1);

            var request = StockAdjustRequestDto.builder()
                    .action("SUBTRACT")
                    .quantity(3)
                    .build();

            assertThatThrownBy(() -> stockService.adjustProduct(productId, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("재고가 부족합니다");

            verify(stockDao).getAvailableQty(productId);
            verify(stockDao, never()).insertStockAdjustment(any());
        }

        @Test
        @DisplayName("SUBTRACT 요청은 충분한 재고일 때 StockAdjustment insert 를 호출한다")
        void subtractAction_insertsAdjustment() {
            int productId = 9;
            LocalDateTime date = LocalDateTime.of(2025, 4, 5, 12, 0);

            when(productDao.getByNum(productId)).thenReturn(
                    ProductDto.builder().productId(productId).codeBId("SUPPLEMENT").build()
            );
            when(stockDao.getAvailableQty(productId)).thenReturn(10);
            when(stockDao.insertStockAdjustment(any(StockAdjustmentDto.class))).thenReturn(1);

            var request = StockAdjustRequestDto.builder()
                    .action("SUBTRACT")
                    .quantity(3)
                    .notes("테스트 차감")
                    .date(date)
                    .build();

            stockService.adjustProduct(productId, request);

            ArgumentCaptor<StockAdjustmentDto> captor = ArgumentCaptor.forClass(StockAdjustmentDto.class);
            verify(stockDao).insertStockAdjustment(captor.capture());
            StockAdjustmentDto dto = captor.getValue();
            assertThat(dto.getProductId()).isEqualTo(productId);
            assertThat(dto.getCodeBId()).isEqualTo("SUPPLEMENT");
            assertThat(dto.getQuantity()).isEqualTo(3);
            assertThat(dto.getNotes()).isEqualTo("테스트 차감");
            assertThat(dto.getCreatedAt()).isEqualTo(date);
        }
    }
}
