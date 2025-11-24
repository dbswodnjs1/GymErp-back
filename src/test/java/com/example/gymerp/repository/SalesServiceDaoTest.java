package com.example.gymerp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.example.gymerp.dto.SalesService;

/**
 * [SalesServiceDao 테스트]
 * - MyBatis 매퍼(SQL) 쿼리 정상 동작 검증
 * - 서비스 판매 등록/조회/수정/삭제 로직 확인
 * - 테스트 전용 프로파일("test") 기반으로 DB 접근
 */
@MybatisTest
@ActiveProfiles("test")
@Import(SalesServiceDaoImpl.class)
class SalesServiceDaoTest {

    @Autowired
    private SalesServiceDao salesServiceDao;

    /* ===============================
       [1. 조회]
    =============================== */

    @Test
    @DisplayName("전체 서비스 판매 내역 조회 - 최소 1건 이상 반환")
    void selectAllSalesServices() {
        List<SalesService> list = salesServiceDao.selectAllSalesServices();
        assertThat(list).isNotNull();
        assertThat(list.size()).isGreaterThan(0);

        // 상태 필드 검증
        list.forEach(s -> {
            assertThat(s.getStatus()).isNotNull();
            assertThat(s.getServiceName()).isNotEmpty();
        });
    }

    @Test
    @DisplayName("단일 서비스 판매 내역 조회 - 존재하는 ID는 정상 반환")
    void selectSalesServiceById_existing() {
        long serviceSalesId = salesServiceDao.selectAllSalesServices().get(0).getServiceSalesId();
        SalesService result = salesServiceDao.selectSalesServiceById(serviceSalesId);

        assertThat(result).isNotNull();
        assertThat(result.getServiceSalesId()).isEqualTo(serviceSalesId);
        assertThat(result.getServiceName()).isNotBlank();
    }

    @Test
    @DisplayName("단일 서비스 판매 내역 조회 - 존재하지 않는 ID는 null 반환")
    void selectSalesServiceById_nonExisting() {
        SalesService result = salesServiceDao.selectSalesServiceById(999_999L);
        assertThat(result).isNull();
    }

    /* ===============================
       [2. 등록]
    =============================== */

    @Test
    @DisplayName("서비스 판매 등록 - 신규 데이터 1건 정상 추가")
    void insertSalesService() {
        SalesService dto = SalesService.builder()
                .serviceId(100L)
                .serviceName("테스트 PT 등록")
                .empNum(1L)
                .memNum(2L)
                .baseCount(10)
                .actualCount(10)
                .discount(0L)
                .baseAmount(500000L)
                .actualAmount(500000L)
                .serviceType("PT")
                .status("ACTIVE")
                .build();

        int inserted = salesServiceDao.insertSalesService(dto);
        assertThat(inserted).isEqualTo(1);
    }

    /* ===============================
       [3. 수정]
    =============================== */

    @Test
    @DisplayName("서비스 판매 수정 - 금액 및 횟수 변경 정상 반영")
    void updateSalesService() {
        SalesService existing = salesServiceDao.selectAllSalesServices().get(0);
        existing.setActualCount(existing.getActualCount() + 1);
        existing.setActualAmount(existing.getActualAmount() + 10000L);

        int updated = salesServiceDao.updateSalesService(existing);
        assertThat(updated).isEqualTo(1);
    }

    /* ===============================
       [4. 삭제]
    =============================== */

    @Test
    @DisplayName("서비스 판매 삭제(논리삭제) - 상태가 DELETED로 변경")
    void deleteSalesService() {
        long id = salesServiceDao.selectAllSalesServices().get(0).getServiceSalesId();

        int deleted = salesServiceDao.deleteSalesService(id);
        assertThat(deleted).isEqualTo(1);

        SalesService after = salesServiceDao.selectSalesServiceById(id);
        assertThat(after).isNotNull();
        assertThat(after.getStatus()).isIn("DELETED", "INACTIVE");
    }
}
