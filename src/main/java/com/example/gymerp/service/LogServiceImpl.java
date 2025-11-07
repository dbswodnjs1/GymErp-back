package com.example.gymerp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.gymerp.dto.PtLogDto;
import com.example.gymerp.dto.VoucherLogDto;
import com.example.gymerp.repository.LogDao;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    private final LogDao logDao;

    /* ===============================
       [회원권 관련]
    =============================== */

    @Override
    public boolean isVoucherValid(long memNum) {
        return logDao.checkVoucherValid(memNum) > 0;
    }

    @Override
    public VoucherLogDto getVoucherByMember(long memNum) {
        return logDao.selectVoucherByMember(memNum);
    }

    @Override
    @Transactional
    public void insertVoucherLog(VoucherLogDto dto) {
        logDao.insertVoucherLog(dto);
    }

    @Override
    @Transactional
    public void extendVoucherLog(VoucherLogDto dto) {
        logDao.extendVoucherLog(dto);
    }

    @Override
    @Transactional
    public void renewVoucherLog(VoucherLogDto dto) {
        logDao.renewVoucherLog(dto);
    }

    @Override
    @Transactional
    public void partialRefundVoucherLog(VoucherLogDto dto) {
        logDao.partialRefundVoucherLog(dto);
    }

    @Override
    @Transactional
    public void rollbackVoucherLog(VoucherLogDto dto) {
        logDao.rollbackVoucherLog(dto);
    }

    // ✅ 회원권 연장 (endDate + N일)
    @Override
    @Transactional
    public void extendVoucherPeriod(long memNum, int extendDays) {
        Map<String, Object> params = new HashMap<>();
        params.put("memNum", memNum);
        params.put("extendDays", extendDays);
        logDao.extendVoucherPeriod(params);
    }


    /* ===============================
       [PT 로그 관련]
    =============================== */

    @Override
    @Transactional
    public void addPtChargeLog(PtLogDto dto) {
        logDao.insertPtChargeLog(dto);
    }

    @Override
    @Transactional
    public void extendPtLog(PtLogDto dto) {
        logDao.updatePtLogExtension(dto);
    }

    @Override
    @Transactional
    public void addPtPartialRefundLog(PtLogDto dto) {
        logDao.insertPtPartialRefundLog(dto);
    }

    @Override
    @Transactional
    public void addPtFullRefundLog(PtLogDto dto) {
        logDao.insertPtFullRefundLog(dto);
    }

    @Override
    public int getRemainingPtCount(long memNum) {
        return logDao.selectRemainingPtCount(memNum);
    }

    @Override
    public PtLogDto getPtLogByRefundId(long refundId) {
        return logDao.selectPtLogByUsageId(refundId);
    }

    // 판매내역(salesId) 기준 PT 로그 조회
    @Override
    public PtLogDto getPtLogBySalesId(long salesId) {
        return logDao.selectPtLogBySalesId(salesId);
    }

    // 기존 PT 충전 로그의 countChange 수정 (연장 처리)
    @Override
    @Transactional
    public void updatePtChargeCount(PtLogDto dto) {
        logDao.updatePtChargeCount(dto);
    }
}
