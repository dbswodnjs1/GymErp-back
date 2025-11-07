package com.example.gymerp.service;

import java.util.List;
import com.example.gymerp.dto.EmpDto;
import com.example.gymerp.dto.ProductDto;
import com.example.gymerp.dto.ServiceDto;

/**
 * 모달 관련 비즈니스 로직을 정의한 서비스 인터페이스
 * - 각 모달(서비스, 상품, 직원 등)의 조회 및 페이징 관련 기능 담당
 */
public interface ModalService {

    /* ========================================================= [서비스 상품 선택 모달]========================================================= */

    // 서비스 상품 목록 조회
    List<ServiceDto> getServiceModalList(ServiceDto dto);

    // 서비스 상품 전체 개수 조회
    int getServiceModalCount(ServiceDto dto);

    /* ========================================================= [서비스 상품 선택 모달 끝]========================================================= */

    
    

    /* ========================================================= [실물 상품 선택 모달]========================================================= */
    
    
    // 실물 상품 목록 조회
    List<ProductDto> getProductModalList(String keyword, int page, int limit);

    // 실물 상품 전체 개수 조회
    int getProductModalCount(String keyword);

    /* ========================================================= [실물 상품 선택 모달 끝]========================================================= */

    
    /* ========================================================= [직원 선택 모달]========================================================= */    
    
    
    // 직원 목록 조회
    List<EmpDto> getEmployeeModalList(String keyword, int page, int limit);

    // 직원 전체 개수 조회
    int getEmployeeModalCount(String keyword);

    /* ========================================================= [직원 선택 모달 끝]========================================================= */    
}









