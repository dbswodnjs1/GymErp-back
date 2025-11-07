package com.example.gymerp.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import com.example.gymerp.dto.PtLogDto;
import com.example.gymerp.dto.VoucherLogDto;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@Primary
@Repository
@RequiredArgsConstructor
public class LogDaoImpl implements LogDao {

    private final SqlSession sqlSession;

    // ===============================
    // [회원권 관련]
    // ===============================

    // 회원권 유효 여부 확인
    @Override
    public int checkVoucherValid(long memNum) {
        return sqlSession.selectOne("LogMapper.checkVoucherValid", memNum);
    }

    // 회원권 단건 조회
    @Override
    public VoucherLogDto selectVoucherByMember(long memNum) {
        return sqlSession.selectOne("LogMapper.selectVoucherByMember", memNum);
    }

    // 회원권 신규 등록
    @Override
    public int insertVoucherLog(VoucherLogDto dto) {
        return sqlSession.insert("LogMapper.insertVoucherLog", dto);
    }

    // 회원권 기간 연장 (endDate 기준)
    @Override
    public int extendVoucherLog(VoucherLogDto dto) {
        return sqlSession.update("LogMapper.extendVoucherLog", dto);
    }

    // 회원권 만료 후 재시작 (startDate 갱신)
    @Override
    public int renewVoucherLog(VoucherLogDto dto) {
        return sqlSession.update("LogMapper.renewVoucherLog", dto);
    }

    // 회원권 부분환불 (남은 기간 단축)
    @Override
    public int partialRefundVoucherLog(VoucherLogDto dto) {
        return sqlSession.update("LogMapper.partialRefundVoucherLog", dto);
    }

    // 회원권 전체환불 (기존 상태로 복구)
    @Override
    public int rollbackVoucherLog(VoucherLogDto dto) {
        return sqlSession.update("LogMapper.rollbackVoucherLog", dto);
    }

    // 회원권 연장 (endDate + N일)
    @Override
    public int extendVoucherPeriod(Map<String, Object> params) {
        return sqlSession.update("LogMapper.extendVoucherPeriod", params);
    }


    // ===============================
    // [PT 로그 관련]
    // ===============================

    // PT 신규 충전 로그 등록
    @Override
    public int insertPtChargeLog(PtLogDto dto) {
        return sqlSession.insert("LogMapper.insertPtChargeLog", dto);
    }

    // PT 연장 (기존 로그 수정)
    @Override
    public int updatePtLogExtension(PtLogDto dto) {
        return sqlSession.update("LogMapper.updatePtLogExtension", dto);
    }

    // PT 부분환불 로그 등록
    @Override
    public int insertPtPartialRefundLog(PtLogDto dto) {
        return sqlSession.insert("LogMapper.insertPtPartialRefundLog", dto);
    }

    // PT 전체환불 로그 등록
    @Override
    public int insertPtFullRefundLog(PtLogDto dto) {
        return sqlSession.insert("LogMapper.insertPtFullRefundLog", dto);
    }

    // 회원별 남은 PT 횟수 조회
    @Override
    public int selectRemainingPtCount(long memNum) {
        return sqlSession.selectOne("LogMapper.selectRemainingPtCount", memNum);
    }

    // 특정 usageId 기준으로 PT 로그 단건 조회
    @Override
    public PtLogDto selectPtLogByUsageId(long usageId) {
        return sqlSession.selectOne("LogMapper.selectPtLogByUsageId", usageId);
    }

    // 판매내역(salesId) 기준 PT 로그 조회
    @Override
    public PtLogDto selectPtLogBySalesId(long salesId) {
        return sqlSession.selectOne("LogMapper.selectPtLogBySalesId", salesId);
    }

    // 기존 PT 충전 로그의 countChange 수정 (연장 처리)
    @Override
    public int updatePtChargeCount(PtLogDto dto) {
        return sqlSession.update("LogMapper.updatePtChargeCount", dto);
    }


    // ===============================
    // [PT 예약 / 취소 관련]
    // ===============================

    // PT 예약 시 소비 로그 등록
    @Override
    public int insertPtConsumeLog(PtLogDto dto) {
        return sqlSession.insert("LogMapper.insertPtConsumeLog", dto);
    }

    // PT 예약 취소 시 복구 로그 등록
    @Override
    public int insertPtCancelLog(PtLogDto dto) {
        return sqlSession.insert("LogMapper.insertPtCancelLog", dto);
    }

    // PT 등록번호(regId)로 기존 소비 로그 조회
    @Override
    public PtLogDto selectPtLogByRegId(long regId) {
        return sqlSession.selectOne("LogMapper.selectPtLogByRegId", regId);
    }
}
