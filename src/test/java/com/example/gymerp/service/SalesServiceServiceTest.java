package com.example.gymerp.service;

import com.example.gymerp.dto.SalesService;
import com.example.gymerp.dto.PtLogDto;
import com.example.gymerp.dto.VoucherLogDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

/**
 * [SalesServiceService 테스트 목적]
 * - 판매 등록 / 수정 / 삭제 로직 단위 테스트
 * - 실제 DB 연결 (테스트용 H2 또는 test profile)
 * - @SpringBootTest: 전체 Bean 로딩
 * - @Transactional: 테스트 종료 후 자동 롤백
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SalesServiceServiceTest {

    @Autowired
    private SalesServiceService salesServiceService;

    @Autowired
    private LogService logService;

    /* ===============================
       [1️. 회원권 등록 테스트]
    =============================== */
    @Test
    @DisplayName("회원권 등록 시 — 회원권 로그 자동 생성 확인")
    void createVoucherSales_generatesVoucherLog() {
        // given
        SalesService dto = SalesService.builder()
                .serviceName("회원권 1개월")
                .serviceType("VOUCHER")
                .empNum(200L)
                .memNum(100L)
                .actualCount(30)
                .actualAmount(100000L)
                .build();

        // when
        int result = salesServiceService.createSalesService(dto);

        // then
        assertThat(result).isGreaterThan(0);
        VoucherLogDto log = logService.getVoucherByMember(100L);
        assertThat(log).isNotNull();
        assertThat(log.getMemNum()).isEqualTo(100L);
        assertThat(log.getStartDate()).isNotEmpty();
        assertThat(log.getEndDate()).isNotEmpty();
    }

    /* ===============================
       [2️. PT 등록 테스트]
    =============================== */
    @Test
    @DisplayName("PT 등록 시 — PT 로그 자동 생성 확인")
    void createPtSales_generatesPtLog() {
        // given
        SalesService dto = SalesService.builder()
                .serviceName("PT 10회권")
                .serviceType("PT")
                .empNum(200L)
                .memNum(100L)
                .actualCount(10)
                .actualAmount(500000L)
                .build();

        // when
        int result = salesServiceService.createSalesService(dto);

        // then
        assertThat(result).isGreaterThan(0);
        int remaining = logService.getRemainingPtCount(100L);
        assertThat(remaining).isGreaterThan(0);
    }

    /* ===============================
       [3️. PT 수정 테스트 (부분환불)]
    =============================== */
    @Test
    @DisplayName("PT 수정 시 — 부분환불 로그 생성 확인")
    void updatePtSales_generatesPartialRefundLog() {
        // given: 우선 PT 등록
        SalesService dto = SalesService.builder()
                .serviceName("PT 10회권")
                .serviceType("PT")
                .empNum(200L)
                .memNum(100L)
                .actualCount(10)
                .actualAmount(500000L)
                .build();
        salesServiceService.createSalesService(dto);

        // when: 수정(횟수 감소 → 부분환불)
        dto.setActualCount(8);
        int result = salesServiceService.updateSalesService(dto);

        // then
        assertThat(result).isGreaterThan(0);
        PtLogDto refundLog = logService.getPtLogByRefundId(dto.getServiceSalesId());
        assertThat(refundLog).isNotNull();
        assertThat(refundLog.getStatus()).isIn("부분환불", "충전");
    }

    /* ===============================
       [4️. 회원권 삭제 테스트 (rollback)]
    =============================== */
    @Test
    @DisplayName("회원권 삭제 시 — 전체환불 로그(rollback) 생성 확인")
    void deleteVoucherSales_generatesRollbackLog() {
        // given: 회원권 등록
        SalesService dto = SalesService.builder()
                .serviceName("회원권 1개월")
                .serviceType("VOUCHER")
                .empNum(200L)
                .memNum(100L)
                .actualCount(30)
                .actualAmount(100000L)
                .build();
        salesServiceService.createSalesService(dto);

        // when: 삭제 (전체환불)
        int result = salesServiceService.deleteSalesService(dto.getServiceSalesId());

        // then
        assertThat(result).isGreaterThan(0);
        VoucherLogDto log = logService.getVoucherByMember(100L);
        assertThat(log).isNotNull();
    }
}
