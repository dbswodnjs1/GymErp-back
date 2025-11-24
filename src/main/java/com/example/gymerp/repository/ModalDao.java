package com.example.gymerp.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.example.gymerp.dto.EmpDto;
import com.example.gymerp.dto.ProductDto;
import com.example.gymerp.dto.ServiceDto;

@Mapper
public interface ModalDao {

    /* =========================================================
       [서비스 상품 선택 모달]
       - Mapper ID: getServiceModalList / getServiceModalCount
       - 테이블: SERVICE
       - DTO: ServiceDto
       - 조건:
           · ISACTIVE = 1 (활성 상품만)
           · keyword 입력 시 NAME LIKE 검색
           · categoryCodes 존재 시 CODEBID IN 조건 필터
       - 정렬:
           · CODEBID LIKE '%PT%' → 최상위 정렬
           · NAME 내림차순
       - 페이징:
           · ROWNUM BETWEEN startRowNum AND endRowNum
    ========================================================= */
    
    // 서비스 상품 목록 조회
    List<ServiceDto> getServiceModalList(ServiceDto dto);

    // 서비스 상품 전체 개수 조회
    int getServiceModalCount(ServiceDto dto);

    /* =========================================================
       [서비스 상품 선택 모달 끝]
    ========================================================= */


    /* =========================================================
       [아이템(상품) 선택 모달]
       - Mapper ID: getProductModalList / getProductModalCount
       - 테이블: PRODUCT
       - 조건:
           · ISACTIVE = 1 (활성 상품만)
           · keyword 입력 시 NAME LIKE 검색
       - 페이징:
           · OFFSET #{offset} ROWS FETCH NEXT #{limit} ROWS ONLY
    ========================================================= */
    
    // 아이템(상품) 목록 조회
    List<ProductDto> getProductModalList(Map<String, Object> param);

    // 아이템(상품) 전체 개수 조회
    int getProductModalCount(Map<String, Object> param);

    /* =========================================================
       [아이템(상품) 선택 모달 끝]
    ========================================================= */


    /* =========================================================
       [직원 선택 모달]
       - Mapper ID: getEmployeeModalList / getEmployeeModalCount
       - 테이블: EMPLOYEE
       - 조건:
           · EMP_STATUS = 'ACTIVE'
           · keyword 입력 시 이름 또는 이메일 LIKE 검색
       - 페이징:
           · OFFSET #{offset} ROWS FETCH NEXT #{limit} ROWS ONLY
    ========================================================= */

    // 직원 목록 조회 (검색 + 페이징)
    List<EmpDto> getEmployeeModalList(Map<String, Object> param);

    // 직원 전체 개수 조회 (검색)
    int getEmployeeModalCount(Map<String, Object> param);

    /* =========================================================
       [직원 선택 모달 끝]
    ========================================================= */


    /* =========================================================
       [회원 선택 모달]
       - (추후 구현 예정)
       - 회원명, 연락처, 등록일 기준 검색 예정
       - 기본 구조: keyword + 페이징
    ========================================================= */
    
    // (추후 회원 선택 모달용 메서드 추가 예정)

    /* =========================================================
       [회원 선택 모달 끝]
    ========================================================= */

}
