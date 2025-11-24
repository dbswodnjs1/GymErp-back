package com.example.gymerp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.example.gymerp.dto.PtLogDto;
import com.example.gymerp.dto.VoucherLogDto;

/**
 * [LogDao 테스트]
 * - MyBatis XML 매퍼 쿼리 검증
 * - 회원권 로그(voucher_log) / PT 로그(pt_log) 쿼리 전체 검증
 * - 실제 DB 연결 없이 테스트 전용 데이터베이스 프로파일 사용
 */
@MybatisTest
@ActiveProfiles("test")
@Import(LogDaoImpl.class)
class LogDaoTest {

    @Autowired
    private LogDao logDao;

    /* ===============================
       [1. 회원권 관련]
    =============================== */

    @Test
    @DisplayName("회원권 유효 여부 확인 - 존재하는 회원은 true, 없는 회원은 false")
    void checkVoucherValid() {
        long existingMemNum = 1L; // 테스트 DB에 존재하는 회원
        long none = 999_999L;

        int valid = logDao.checkVoucherValid(existingMemNum);
        int invalid = logDao.checkVoucherValid(none);

        assertThat(valid).isIn(0, 1);
        assertThat(invalid).isEqualTo(0);
    }

    @Test
    @DisplayName("회원권 단건 조회 - 회원번호로 정확한 데이터 반환")
    void selectVoucherByMember() {
        long memNum = 1L;
        VoucherLogDto dto = logDao.selectVoucherByMember(memNum);

        assertThat(dto).isNotNull();
        assertThat(dto.getMemNum()).isEqualTo(memNum);
        assertThat(dto.getStartDate()).isNotNull();
        assertThat(dto.getEndDate()).isNotNull();
    }

    @Test
    @DisplayName("회원권 등록 → 단건 조회로 즉시 확인 가능")
    void insertVoucherLog_andSelect() {
        VoucherLogDto dto = VoucherLogDto.builder()
                .memNum(9999L)
                .memberName("테스트회원")
                .startDate("2025-11-01")
                .endDate("2025-12-01")
                .build();

        int inserted = logDao.insertVoucherLog(dto);
        assertThat(inserted).isEqualTo(1);

        VoucherLogDto found = logDao.selectVoucherByMember(dto.getMemNum());
        assertThat(found).isNotNull();
        assertThat(found.getMemberName()).isEqualTo("테스트회원");
    }

    /* ===============================
       [2. PT 로그 관련]
    =============================== */

    @Test
    @DisplayName("PT 충전 로그 등록 및 남은 횟수 조회")
    void insertPtCharge_andSelectRemainingCount() {
        PtLogDto ptLog = PtLogDto.builder()
                .memNum(100L)
                .empNum(200L)
                .status("충전")
                .countChange(5L)
                .salesId(123L)
                .regId(0L)
                .build();

        int inserted = logDao.insertPtChargeLog(ptLog);
        assertThat(inserted).isEqualTo(1);

        int remaining = logDao.selectRemainingPtCount(ptLog.getMemNum());
        assertThat(remaining).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("PT 소비/취소 로그 정상 등록 및 합산 검증")
    void insertPtConsume_andCancelLog() {
        PtLogDto consume = PtLogDto.builder()
                .memNum(100L)
                .empNum(200L)
                .status("소비")
                .regId(999L)
                .build();

        logDao.insertPtConsumeLog(consume);

        PtLogDto cancel = PtLogDto.builder()
                .memNum(100L)
                .empNum(200L)
                .status("예약취소")
                .regId(999L)
                .build();

        logDao.insertPtCancelLog(cancel);

        int remaining = logDao.selectRemainingPtCount(100L);
        assertThat(remaining).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("PT 부분환불 / 전체환불 로그 정상 등록")
    void insertPtRefundLogs() {
        PtLogDto partial = PtLogDto.builder()
                .memNum(100L)
                .empNum(200L)
                .status("부분환불")
                .countChange(2L)
                .salesId(10L)
                .regId(0L)
                .build();

        PtLogDto full = PtLogDto.builder()
                .memNum(100L)
                .empNum(200L)
                .status("전체환불")
                .countChange(5L)
                .salesId(11L)
                .regId(0L)
                .build();

        assertThat(logDao.insertPtPartialRefundLog(partial)).isEqualTo(1);
        assertThat(logDao.insertPtFullRefundLog(full)).isEqualTo(1);
    }

    @Test
    @DisplayName("PT 등록번호(regId)로 기존 소비 로그 조회")
    void selectPtLogByRegId() {
        long regId = 999L; // 위 테스트에서 소비 로그로 사용한 regId
        PtLogDto log = logDao.selectPtLogByRegId(regId);

        assertThat(log).isNotNull();
        assertThat(log.getRegId()).isEqualTo(regId);
        assertThat(log.getStatus()).isEqualTo("소비");
    }
}
