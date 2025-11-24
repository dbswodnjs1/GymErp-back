package com.example.gymerp.scenario;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.gymerp.dto.StockAdjustRequestDto;
import com.example.gymerp.repository.StockDao;
import com.example.gymerp.service.StockService;

/**
 * [시나리오 통합 테스트]
 * - 프로파일: test (대개 H2)
 * - 목적: 재고 조정(ADD/SUBTRACT) 시 실제 INSERT 흐름이 테이블에 반영되는지 검증
 * - 전제: 스키마 네이밍은 head 기준으로 'SALES_ITEM' 사용
 */
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class StockServiceIntegrationTest {

    @Autowired private StockService stockService;
    @Autowired private StockDao stockDao;
    @Autowired private JdbcTemplate jdbcTemplate;

    private Map<String, Object> pageParams(int productId) {
        Map<String, Object> params = new HashMap<>();
        params.put("productId", productId);
        params.put("offset", 0);
        params.put("size", 20);
        params.put("startDate", null);
        params.put("endDate", null);
        return params;
    }

    /**
     * 매 테스트 전에 최소 더미 데이터와 시퀀스 상태를 정돈
     * - H2/Oracle 혼용 시도(정보 조회는 예외시 대안쿼리 사용)
     * - CODEA/CODEB/PRODUCT upsert
     * - 시퀀스 RESTART (H2 기준 문법 사용)
     */
    @BeforeEach
    void setupProduct() {
        // (선택) 현재 DB에서 관련 테이블 보이는지 출력 (H2/Oracle 혼용)
        try {
            List<String> tables = jdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM USER_TABLES WHERE TABLE_NAME IN ('SALESITEM','SALES_ITEM')",
                String.class
            );
            tables.forEach(t -> System.out.println("📦 USER_TABLES.TABLE: " + t));
        } catch (Exception e) {
            List<String> tables = jdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
                "WHERE TABLE_NAME IN ('SALESITEM','SALES_ITEM')",
                String.class
            );
            tables.forEach(t -> System.out.println("📦 INFORMATION_SCHEMA.TABLE: " + t));
        }

        // (선택) H2 버전 확인
        try {
            String v = jdbcTemplate.queryForObject("SELECT H2VERSION() FROM DUAL", String.class);
            System.out.println("H2 VERSION = " + v);
        } catch (Exception ignore) {
            System.out.println("Not H2 (H2VERSION() unavailable).");
        }

        // ===== 기본 코드/상품 더미 구성 =====
        // CODEA upsert
        Integer codeACount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM CODEA WHERE CODEAID = 'PRODUCT_TYPE'",
            Integer.class
        );
        if (codeACount == null || codeACount == 0) {
            jdbcTemplate.update("""
                INSERT INTO CODEA (CODEAID, NAME)
                VALUES ('PRODUCT_TYPE', '상품유형')
            """);
        }

        // CODEB upsert
        Integer codeBCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM CODEB WHERE CODEBID = 'SUPPLEMENT'",
            Integer.class
        );
        if (codeBCount == null || codeBCount == 0) {
            jdbcTemplate.update("""
                INSERT INTO CODEB (CODEBID, CODEAID, NAME)
                VALUES ('SUPPLEMENT', 'PRODUCT_TYPE', '보충제')
            """);
        }

        // PRODUCT upsert (productId=1)
        Integer productCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM PRODUCT WHERE PRODUCTID = 1",
            Integer.class
        );
        if (productCount == null || productCount == 0) {
            jdbcTemplate.update("""
                INSERT INTO PRODUCT (PRODUCTID, NAME, CODEBID, PRICE, CREATEDAT)
                VALUES (1, '테스트상품', 'SUPPLEMENT', 10000, SYSTIMESTAMP)
            """);
        }

        // ===== 시퀀스 RESTART (test/H2 기준) =====
        // STOCKADJUSTMENT -> STOCK_ADJUSTMENT_SEQ
        Integer nextAdj = jdbcTemplate.queryForObject(
            "SELECT COALESCE(MAX(ADJUSTMENTID),0) + 1 FROM STOCKADJUSTMENT", Integer.class);
        jdbcTemplate.execute("ALTER SEQUENCE STOCK_ADJUSTMENT_SEQ RESTART WITH " + nextAdj);

        // PURCHASE -> PURCHASE_SEQ
        Integer nextPur = jdbcTemplate.queryForObject(
            "SELECT COALESCE(MAX(PURCHASEID),0) + 1 FROM PURCHASE", Integer.class);
        jdbcTemplate.execute("ALTER SEQUENCE PURCHASE_SEQ RESTART WITH " + nextPur);

        // SALES_ITEM -> SALES_ITEM_SEQ  (※ head 기준으로 SALES_ITEM 사용)
        Integer nextSi = jdbcTemplate.queryForObject(
            "SELECT COALESCE(MAX(ITEMSALESID),0) + 1 FROM SALES_ITEM", Integer.class);
        jdbcTemplate.execute("ALTER SEQUENCE SALES_ITEM_SEQ RESTART WITH " + nextSi);
    }

    @Test
    @DisplayName("✅ [ADD] 요청 시 Purchase 테이블에 INSERT 된다")
    @Rollback(true) // 테스트 종료 후 롤백
    void addStock_insertsPurchaseRecord() {
        // given
        int productId = 1;
        var req = StockAdjustRequestDto.builder()
                .action("ADD")
                .quantity(3)
                .notes("테스트 입고")
                .build();

        // when
        stockService.adjustProduct(productId, req);

        // then
        var purchaseList = stockDao.getPurchaseList(pageParams(productId));
        assertThat(purchaseList).isNotEmpty();
        System.out.println("✅ Purchase insert 성공! size=" + purchaseList.size());
    }

    @Test
    @DisplayName("✅ [SUBTRACT] 요청 시 StockAdjustment 테이블에 INSERT 된다")
    @Rollback(true) // 필요시 false로 바꿔 실제 반영 확인 가능
    void subtractStock_insertsAdjustmentRecord() {
        // given
        int productId = 1;

        // 차감 테스트는 가용재고가 필요하므로 선행 ADD
        stockService.adjustProduct(productId, StockAdjustRequestDto.builder()
                .action("ADD").quantity(5).notes("선행 입고").build());

        var req = StockAdjustRequestDto.builder()
                .action("SUBTRACT")
                .quantity(2)
                .notes("테스트 차감")
                .build();

        // when
        stockService.adjustProduct(productId, req);

        // then
        var adjList = stockDao.getAdjustStockAndSalesList(pageParams(productId));
        assertThat(adjList).isNotEmpty();
        System.out.println("✅ StockAdjustment insert 성공! size=" + adjList.size());
    }
}
