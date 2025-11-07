package com.example.gymerp.service;

import com.example.gymerp.dto.PtLogDto;
import com.example.gymerp.dto.VoucherLogDto;

public interface LogService {

    /* ===============================
       [회원권 관련]
    =============================== */

    // 회원권 유효 여부 확인
    boolean isVoucherValid(long memNum);

    // 회원권 단건 조회
    VoucherLogDto getVoucherByMember(long memNum);

    // 회원권 신규 등록
    void insertVoucherLog(VoucherLogDto dto);

    // 회원권 기간 연장 (endDate 기준 누적)
    void extendVoucherLog(VoucherLogDto dto);

    // 회원권 만료 후 재시작
    void renewVoucherLog(VoucherLogDto dto);

    // 회원권 부분환불 (endDate 단축)
    void partialRefundVoucherLog(VoucherLogDto dto);

    // 회원권 전체환불 (이전 상태로 롤백)
    void rollbackVoucherLog(VoucherLogDto dto);

    // 회원권 연장 (endDate + N일)
    void extendVoucherPeriod(long memNum, int extendDays);



    /* ===============================
       [PT 로그 관련]
    =============================== */

    // PT 신규 충전 로그 등록
    void addPtChargeLog(PtLogDto dto);

    // PT 연장 (기존 row update)
    void extendPtLog(PtLogDto dto);

    // PT 부분환불 로그 등록
    void addPtPartialRefundLog(PtLogDto dto);

    // PT 전체환불 로그 등록
    void addPtFullRefundLog(PtLogDto dto);

    // 남은 PT 횟수 조회
    int getRemainingPtCount(long memNum);

    // 특정 판매건의 PT 로그 조회 (refundId 기준)
    PtLogDto getPtLogByRefundId(long refundId);

    // 특정 판매내역(salesId) 기준 PT 로그 조회
    PtLogDto getPtLogBySalesId(long salesId);

    // 기존 PT 충전 로그의 countChange 수정 (연장 처리)
    void updatePtChargeCount(PtLogDto dto);
}
