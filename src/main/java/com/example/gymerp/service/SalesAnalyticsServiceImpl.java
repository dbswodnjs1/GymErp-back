package com.example.gymerp.service;

import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.gymerp.repository.SalesAnalyticsDao;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SalesAnalyticsServiceImpl implements SalesAnalyticsService {

    private final SalesAnalyticsDao salesAnalyticsDao;

    /* =========================================================
       [전체 매출 그래프]
       - 서비스 + 실물 상품 매출 합산
       - 필터 없음 (전체기간 기준)
    ========================================================= */
    @Override
    public List<Map<String, Object>> getTotalSalesChart() {
        return salesAnalyticsDao.selectTotalSalesChart();
    }

    /* =========================================================
       [서비스 매출 그래프]
       - sales_service 기준
       - 필터 없음 (전체기간 기준)
    ========================================================= */
    @Override
    public List<Map<String, Object>> getServiceSalesChart() {
        return salesAnalyticsDao.selectServiceSalesChart();
    }

    /* =========================================================
       [실물 상품 매출 그래프]
       - sales_item 기준
       - 필터 없음 (전체기간 기준)
    ========================================================= */
    @Override
    public List<Map<String, Object>> getItemSalesChart() {
        return salesAnalyticsDao.selectItemSalesChart();
    }

    /* =========================================================
       [트레이너 실적 그래프]
       - 지난달 기준, 상위 3명
       - sales_service + employee 조인
    ========================================================= */
    @Override
    public List<Map<String, Object>> getTrainerPerformanceChart() {
        return salesAnalyticsDao.selectTrainerPerformanceChart();
    }

    /* =========================================================
       [AI 회원수 예측 그래프]
       - 월 기준 정렬
    ========================================================= */
    @Override
    public List<Map<String, Object>> getAiMemberPredictionChart() {
        try {
            List<Map<String, Object>> result = salesAnalyticsDao.selectAiMemberPredictions();
            if (result == null || result.isEmpty()) {
                throw new IllegalStateException("예측 데이터가 존재하지 않습니다. (ai_member_prediction 테이블 확인 필요)");
            }
            result.sort(Comparator.comparing(m -> String.valueOf(m.get("month"))));
            return result;
        } catch (Exception e) {
            throw new RuntimeException("AI 회원 예측 조회 실패: " + e.getMessage(), e);
        }
    }

    /* =========================================================
       [AI 매출 예측 그래프]
       - 연도/월 기준 정렬
    ========================================================= */
    @Override
    public List<Map<String, Object>> getAiSalesPredictionChart() {
        try {
            List<Map<String, Object>> result = salesAnalyticsDao.selectAiSalesPredictions();
            if (result == null || result.isEmpty()) {
                throw new IllegalStateException("예측 데이터가 존재하지 않습니다. (ai_sales_prediction 테이블 확인 필요)");
            }

            // 정렬: year + month 기준
            result.sort(Comparator.comparing(
                m -> (String.valueOf(m.get("year")) + String.valueOf(m.get("month")))
            ));

            return result;
        } catch (Exception e) {
            throw new RuntimeException("AI 매출 예측 조회 실패: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Map<String, Object>> getActualSales3Years() {
        return salesAnalyticsDao.selectActualSales3Years();
    }

    @Override
    public List<Map<String, Object>> getSalesWithPrediction() {
        return salesAnalyticsDao.selectSalesWithPrediction();
    }
}
