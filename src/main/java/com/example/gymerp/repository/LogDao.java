package com.example.gymerp.repository;

import com.example.gymerp.dto.PtLogDto;
import com.example.gymerp.dto.VoucherLogDto;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface LogDao {

    // ===============================
    // [회원권 관련]
    // ===============================

    // 회원권 유효 여부 확인
    int checkVoucherValid(long memNum);

    // 회원권 단건 조회
    VoucherLogDto selectVoucherByMember(long memNum);

    // 회원권 신규 등록
    int insertVoucherLog(VoucherLogDto dto);

    // 회원권 기간 연장 (endDate 기준 누적 업데이트)
    int extendVoucherLog(VoucherLogDto dto);

    // 회원권 만료 후 재시작 (startDate = SYSDATE)
    int renewVoucherLog(VoucherLogDto dto);

    // 회원권 부분환불 (endDate 재조정)
    int partialRefundVoucherLog(VoucherLogDto dto);

    // 회원권 전체환불 (이전 상태로 롤백)
    int rollbackVoucherLog(VoucherLogDto dto);

    // 회원권 연장 (기존 endDate + N일 추가)
    int extendVoucherPeriod(Map<String, Object> params);



    // ===============================
    // [PT 로그 관련]
    // ===============================

    // PT 신규 충전 로그 등록
    int insertPtChargeLog(PtLogDto dto);

    // PT 연장 (기존 row update)
    int updatePtLogExtension(PtLogDto dto);

    // PT 부분환불 로그 (새로운 row insert)
    int insertPtPartialRefundLog(PtLogDto dto);

    // PT 전체환불 로그 (새로운 row insert)
    int insertPtFullRefundLog(PtLogDto dto);

    // 남은 PT 횟수 조회
    int selectRemainingPtCount(long memNum);

    // 특정 판매건의 PT 로그 찾기 (usageId 기준)
    PtLogDto selectPtLogByUsageId(long usageId);

    // 판매내역(salesId) 기준으로 PT 충전 로그 조회
    PtLogDto selectPtLogBySalesId(long salesId);

    // 기존 PT 충전 로그의 countChange 수정 (연장 처리)
    int updatePtChargeCount(PtLogDto dto);



    // ===============================
    // [PT 예약 / 취소 관련]
    // ===============================

    // PT 예약 시 (소비 로그 등록)
    int insertPtConsumeLog(PtLogDto dto);

    // PT 예약 취소 시 (복구 로그 등록)
    int insertPtCancelLog(PtLogDto dto);

    // PT 등록번호(regId)로 기존 소비 로그 조회
    PtLogDto selectPtLogByRegId(long regId);
}
