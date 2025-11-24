package com.example.gymerp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.example.gymerp.dto.CurrentStockDto;
import com.example.gymerp.dto.PurchaseDto;
import com.example.gymerp.dto.StockAdjustmentDto;

@MybatisTest
@ActiveProfiles("test")
@Import(StockDaoImpl.class)
class StockDaoTest {

    @Autowired
    private StockDao stockDao;

    private Map<String, Object> baseParams(int productId) {
        Map<String, Object> params = new HashMap<>();
        params.put("productId", productId);
        params.put("offset", 0);
        params.put("size", 20);
        params.put("startDate", null);
        params.put("endDate", null);
        return params;
    }

    private int existingProductId() {
        List<CurrentStockDto> firstPage = stockDao.getCurrentStockListPaged(0, 1, null);
        assertThat(firstPage).as("재고 목록 선행 조회").isNotEmpty();
        return firstPage.get(0).getProductId();
    }

    @Test
    @DisplayName("getCurrentStockListPaged는 이름 오름차순으로 정렬된 데이터를 반환한다")
    void currentStockList_basic() {
        List<CurrentStockDto> list = stockDao.getCurrentStockListPaged(0, 50, null);

        assertThat(list).isNotEmpty();
        for (int i = 1; i < list.size(); i++) {
            String prev = list.get(i - 1).getProductName();
            String current = list.get(i).getProductName();
            if (prev != null && current != null) {
                assertThat(prev).isLessThanOrEqualTo(current);
            }
        }
    }

    @Test
    @DisplayName("getPurchaseList는 count와 목록이 동일한 조건으로 조회된다")
    void purchaseList_matchesCountAndSort() {
        int productId = existingProductId();
        Map<String, Object> params = baseParams(productId);

        int total = stockDao.getPurchaseListCount(params);
        List<PurchaseDto> list = stockDao.getPurchaseList(params);

        assertThat(total).isGreaterThanOrEqualTo(list.size());
        if (list.size() > 1) {
            for (int i = 1; i < list.size(); i++) {
                assertThat(list.get(i - 1).getCreatedAt())
                        .as("createdAt desc")
                        .isAfterOrEqualTo(list.get(i).getCreatedAt());
            }
        }
    }

    @Test
    @DisplayName("getPurchaseList는 존재하지 않는 상품에 대해 빈 결과를 반환한다")
    void purchaseList_nonExisting() {
        Map<String, Object> params = baseParams(999_999);

        assertThat(stockDao.getPurchaseListCount(params)).isZero();
        assertThat(stockDao.getPurchaseList(params)).isEmpty();
    }

    @Test
    @DisplayName("getAdjustStockAndSalesList는 count와 목록이 동일한 조건으로 조회된다")
    void outboundList_matchesCount() {
        int productId = existingProductId();
        Map<String, Object> params = baseParams(productId);

        int total = stockDao.getAdjustStockAndSalesListCount(params);
        List<StockAdjustmentDto> list = stockDao.getAdjustStockAndSalesList(params);

        assertThat(total).isGreaterThanOrEqualTo(list.size());
        list.forEach(dto -> {
            assertThat(dto.getProductId()).isEqualTo(productId);
            assertThat(dto.getQuantity()).isNotNull();
        });
    }

    @Test
    @DisplayName("getAdjustStockAndSalesList는 존재하지 않는 상품에 대해 빈 결과를 반환한다")
    void outboundList_nonExisting() {
        Map<String, Object> params = baseParams(999_999);

        assertThat(stockDao.getAdjustStockAndSalesListCount(params)).isZero();
        assertThat(stockDao.getAdjustStockAndSalesList(params)).isEmpty();
    }
}
