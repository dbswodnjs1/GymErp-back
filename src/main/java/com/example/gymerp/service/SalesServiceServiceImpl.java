package com.example.gymerp.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.gymerp.dto.PtLogDto;
import com.example.gymerp.dto.SalesService;
import com.example.gymerp.dto.VoucherLogDto;
import com.example.gymerp.repository.EmpDao;
import com.example.gymerp.repository.LogDao;
import com.example.gymerp.repository.MemberDao;
import com.example.gymerp.repository.SalesServiceDao;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class SalesServiceServiceImpl implements SalesServiceService {

    private final SalesServiceDao salesServiceDao;
    private final LogService logService;
    private final MemberDao memberDao;
    private final EmpDao empDao;
    private final LogDao logDao;

    /* ===============================
       [1. 조회]
    =============================== */

    @Override
    public List<SalesService> getAllSalesServices() {
        return salesServiceDao.selectAllSalesServices();
    }

    @Override
    public SalesService getSalesServiceById(long serviceSalesId) {
        return salesServiceDao.selectSalesServiceById(serviceSalesId);
    }

    /* ===============================
       [2. 등록]
    =============================== */

    @Override
    @Transactional
    public int createSalesService(SalesService salesService) {
        String memberName = memberDao.selectMemberNameById(salesService.getMemNum().intValue());
        String trainerName = empDao.selectEmployeeNameById(salesService.getEmpNum().intValue());

        //  판매 내역을 먼저 등록 (시퀀스 생성)
        salesServiceDao.insertSalesService(salesService);

        // 방금 생성된 PK
        Long newSalesId = salesService.getServiceSalesId();
        
        if ("VOUCHER".equalsIgnoreCase(salesService.getServiceType())) {
            boolean hasValidVoucher = logService.isVoucherValid(salesService.getMemNum());
            LocalDate start = LocalDate.now();
            LocalDate end = start.plusDays(salesService.getActualCount());

            VoucherLogDto voucher = VoucherLogDto.builder()
                    .memNum(salesService.getMemNum())
                    .memberName(memberName)
                    .startDate(start.toString())
                    .endDate(end.toString())
                    .build();

            if (!hasValidVoucher) logService.insertVoucherLog(voucher);
            else logService.extendVoucherLog(voucher);
        } else if ("PT".equalsIgnoreCase(salesService.getServiceType())) {
            if (!logService.isVoucherValid(salesService.getMemNum()))
                throw new IllegalStateException("회원권이 유효하지 않아 PT 등록이 불가합니다.");

            PtLogDto ptLog = PtLogDto.builder()
                    .memNum(salesService.getMemNum())
                    .empNum(salesService.getEmpNum())
                    .status("충전")
                    .countChange(Long.valueOf(salesService.getActualCount()))
                    .salesId(newSalesId)
                    .createdAt(LocalDateTime.now())
                    .build();

            logService.addPtChargeLog(ptLog);
            
            // refundId
            Long refundId = ptLog.getUsageId(); 
            if (refundId != null) {
                Map<String, Object> param = new HashMap<>();
                param.put("serviceSalesId", newSalesId);
                param.put("refundId", refundId);
                salesServiceDao.updateRefundIdBySalesId(param);
            }
        }

        return 1;
    }

    /* ===============================
    [3. 수정]
 =============================== */

 @Override
 @Transactional
 public int updateSalesService(SalesService salesService) {
     SalesService existing = salesServiceDao.selectSalesServiceById(salesService.getServiceSalesId());
     if (existing == null)
         throw new IllegalArgumentException("해당 판매 내역이 존재하지 않습니다.");
     
     if (!existing.getServiceType().equalsIgnoreCase(salesService.getServiceType()))
         throw new IllegalStateException("상품 타입(PT/회원권)은 수정할 수 없습니다.");
     
     System.out.println("=== [DEBUG] updateSalesService() 진입 ===");
     System.out.println("기존 서비스 타입: " + existing.getServiceType());
     System.out.println("기존 판매ID(serviceSalesId): " + existing.getServiceSalesId());

     /* ===============================
	        [A] PT 상품 (횟수 변경)
	     =============================== */
	     if ("PT".equalsIgnoreCase(existing.getServiceType())) {
	         int oldCount = existing.getActualCount();
	         int newCount = salesService.getActualCount();
	
	         // [1] 부분환불 (횟수 감소)
	         if (newCount < oldCount) {
	             int refundCount = oldCount - newCount;
	             PtLogDto refundLog = PtLogDto.builder()
	                     .memNum(existing.getMemNum())
	                     .empNum(existing.getEmpNum())
	                     .status("부분환불")
	                     .countChange(Long.valueOf(-refundCount))
	                     .salesId(existing.getServiceSalesId())
	                     .createdAt(LocalDateTime.now())
	                     .build();
	
	             logService.addPtPartialRefundLog(refundLog);
	         }
	
	         // [2] 연장 (횟수 증가)
	         else if (newCount > oldCount) {
	             int addCount = newCount - oldCount;
	
	             // 기존 충전 로그 조회
	             PtLogDto existingLog = logService.getPtLogBySalesId(existing.getServiceSalesId());
	             System.out.println("[DEBUG] existingLog = " + existingLog);
	             if (existingLog != null && "충전".equals(existingLog.getStatus())) {
	                 long updatedCount = existingLog.getCountChange() + addCount;
	                 existingLog.setCountChange(updatedCount);
	
	                 logService.updatePtChargeCount(existingLog);
	             }
	         }
	     }
	
	     /* ===============================
	        [B] VOUCHER 상품 (일수 변경)
	     =============================== */
	     else if ("VOUCHER".equalsIgnoreCase(existing.getServiceType())) {
	         VoucherLogDto voucher = logService.getVoucherByMember(existing.getMemNum());
	         if (voucher != null) {
	             int oldDays = existing.getBaseCount();
	             int newDays = salesService.getBaseCount();
	
	             // [1] 부분환불 (일수 감소)
	             if (newDays < oldDays) {
	                 LocalDate endDate = LocalDate.parse(voucher.getEndDate());
	                 LocalDate newEndDate = endDate.minusDays(oldDays - newDays);
	                 voucher.setEndDate(newEndDate.toString());
	                 logService.partialRefundVoucherLog(voucher);
	             }
	
	             // [2] 연장 (일수 증가)
	             else if (newDays > oldDays) {
	                 LocalDate endDate = LocalDate.parse(voucher.getEndDate());
	                 LocalDate newEndDate = endDate.plusDays(newDays - oldDays);
	                 voucher.setEndDate(newEndDate.toString());
	                 logService.extendVoucherLog(voucher);
	             }
	         }
	     }
	
	     return salesServiceDao.updateSalesService(salesService);
	 }


    /* ===============================
       [4. 삭제]
    =============================== */

    @Override
    @Transactional
    public int deleteSalesService(long serviceSalesId) {
        SalesService sale = salesServiceDao.selectSalesServiceById(serviceSalesId);
        if (sale == null)
            throw new IllegalArgumentException("해당 판매 내역이 존재하지 않습니다.");

        if ("PT".equalsIgnoreCase(sale.getServiceType())) {
            int remaining = logService.getRemainingPtCount(sale.getMemNum());
            if (remaining < sale.getActualCount())
                throw new IllegalStateException("이미 일부 이용된 PT는 삭제할 수 없습니다.");

            PtLogDto refundLog = PtLogDto.builder()
                    .memNum(sale.getMemNum())
                    .empNum(sale.getEmpNum())
                    .status("전체환불")
                    .countChange(Long.valueOf(-sale.getActualCount()))
                    .salesId(sale.getServiceSalesId())
                    .createdAt(LocalDateTime.now())
                    .build();

            logService.addPtFullRefundLog(refundLog);
        } else if ("VOUCHER".equalsIgnoreCase(sale.getServiceType())) {
            VoucherLogDto voucher = logService.getVoucherByMember(sale.getMemNum());
            if (voucher != null) logService.rollbackVoucherLog(voucher);
        }

        return salesServiceDao.deleteSalesService(serviceSalesId);
    }

    /* ===============================
       [5. 내역 조회 (필터 + 스크롤)]
    =============================== */

    @Override
    public int getSalesServiceCount(Map<String, Object> params) {
        return salesServiceDao.selectSalesServiceCount(params);
    }

    @Override
    public List<SalesService> getPagedSalesServices(Map<String, Object> params) {
        return salesServiceDao.selectPagedSalesServices(params);
    }

    /* ===============================
       [6. 서비스 매출 통계 조회]
    =============================== */

    @Override
    public List<Map<String, Object>> getServiceSalesAnalytics(
            String startDate,
            String endDate,
            String serviceNameKeyword,
            Integer memNum,
            Integer empNum) {

        Map<String, Object> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("serviceNameKeyword", serviceNameKeyword);
        params.put("memNum", memNum);
        params.put("empNum", empNum);

        return salesServiceDao.selectServiceSalesAnalytics(params);
    }
}
