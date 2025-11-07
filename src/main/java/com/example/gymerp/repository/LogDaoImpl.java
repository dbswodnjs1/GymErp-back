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

    @Override
    public int checkVoucherValid(long memNum) {
        return sqlSession.selectOne("LogMapper.checkVoucherValid", memNum);
    }

    @Override
    public VoucherLogDto selectVoucherByMember(long memNum) {
        return sqlSession.selectOne("LogMapper.selectVoucherByMember", memNum);
    }

    @Override
    public int insertVoucherLog(VoucherLogDto dto) {
        return sqlSession.insert("LogMapper.insertVoucherLog", dto);
    }

    @Override
    public int extendVoucherLog(VoucherLogDto dto) {
        return sqlSession.update("LogMapper.extendVoucherLog", dto);
    }

    @Override
    public int renewVoucherLog(VoucherLogDto dto) {
        return sqlSession.update("LogMapper.renewVoucherLog", dto);
    }

    @Override
    public int partialRefundVoucherLog(VoucherLogDto dto) {
        return sqlSession.update("LogMapper.partialRefundVoucherLog", dto);
    }

    @Override
    public int rollbackVoucherLog(VoucherLogDto dto) {
        return sqlSession.update("LogMapper.rollbackVoucherLog", dto);
    }

    @Override
    public int extendVoucherPeriod(Map<String, Object> params) {
        return sqlSession.update("LogMapper.extendVoucherPeriod", params);
    }

    @Override
    public VoucherLogDto selectPreviousVoucherByMember(long memNum) {
        return sqlSession.selectOne("LogMapper.selectPreviousVoucherByMember", memNum);
    }



    // ===============================
    // [PT 로그 관련]
    // ===============================

    @Override
    public int insertPtChargeLog(PtLogDto dto) {
        return sqlSession.insert("LogMapper.insertPtChargeLog", dto);
    }

    @Override
    public int insertPtPartialRefundLog(PtLogDto dto) {
        return sqlSession.insert("LogMapper.insertPtPartialRefundLog", dto);
    }

    @Override
    public int insertPtFullRefundLog(PtLogDto dto) {
        return sqlSession.insert("LogMapper.insertPtFullRefundLog", dto);
    }

    @Override
    public int selectRemainingPtCount(long memNum) {
        return sqlSession.selectOne("LogMapper.selectRemainingPtCount", memNum);
    }

    @Override
    public PtLogDto getPtLogBySalesId(long salesId) {
        return sqlSession.selectOne("LogMapper.getPtLogBySalesId", salesId);
    }

    @Override
    public PtLogDto selectPtLogByUsageId(long usageId) {
        return sqlSession.selectOne("LogMapper.selectPtLogByUsageId", usageId);
    }

    @Override
    public int updatePtChargeCount(PtLogDto dto) {
        return sqlSession.update("LogMapper.updatePtChargeCount", dto);
    }

    @Override
    public int checkPtTurnReached(Map<String, Object> params) {
        return sqlSession.selectOne("LogMapper.checkPtTurnReached", params);
    }



    // ===============================
    // [PT 예약 / 취소 관련]
    // ===============================

    @Override
    public int insertPtConsumeLog(PtLogDto dto) {
        return sqlSession.insert("LogMapper.insertPtConsumeLog", dto);
    }

    @Override
    public int insertPtCancelLog(PtLogDto dto) {
        return sqlSession.insert("LogMapper.insertPtCancelLog", dto);
    }

    @Override
    public PtLogDto selectPtLogByRegId(long regId) {
        return sqlSession.selectOne("LogMapper.selectPtLogByRegId", regId);
    }
}
