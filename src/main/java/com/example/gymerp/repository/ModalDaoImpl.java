package com.example.gymerp.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import com.example.gymerp.dto.EmpDto;
import com.example.gymerp.dto.ProductDto;
import com.example.gymerp.dto.ServiceDto;

import lombok.RequiredArgsConstructor;

@Primary
@Repository
@RequiredArgsConstructor
public class ModalDaoImpl implements ModalDao {

    private final SqlSession sqlSession;

    /* =========================================================
       [서비스 상품 선택 모달]
       - Mapper ID: getServiceModalList / getServiceModalCount
       - 조건:
           · ISACTIVE = 1 (활성 상품만)
           · keyword 입력 시 NAME LIKE 검색
           · categoryCodes 존재 시 CODEBID IN 조건 필터
       - 정렬:
           · CODEBID LIKE '%PT%' → 최상위
           · NAME 내림차순
       - 페이징:
           · ROWNUM BETWEEN startRowNum AND endRowNum
    ========================================================= */
    @Override
    public List<ServiceDto> getServiceModalList(ServiceDto dto) {
        return sqlSession.selectList("ModalMapper.getServiceModalList", dto);
    }

    @Override
    public int getServiceModalCount(ServiceDto dto) {
        return sqlSession.selectOne("ModalMapper.getServiceModalCount", dto);
    }

    /* =========================================================
       [서비스 상품 선택 모달 끝]
    ========================================================= */


    /* =========================================================
       [실물 상품 선택 모달]
       - Mapper ID: getProductModalList / getProductModalCount
       - 조건:
           · ISACTIVE = 1 (활성 상품만)
           · keyword 입력 시 NAME LIKE 검색
       - 페이징:
           · ROWNUM BETWEEN startRow AND endRow (11g 호환)
    ========================================================= */
    @Override
    public List<ProductDto> getProductModalList(Map<String, Object> param) {
        return sqlSession.selectList("ModalMapper.getProductModalList", param);
    }

    @Override
    public int getProductModalCount(Map<String, Object> param) {
        return sqlSession.selectOne("ModalMapper.getProductModalCount", param);
    }

    /* =========================================================
       [실물 상품 선택 모달 끝]
    ========================================================= */


    /* =========================================================
       [직원 선택 모달]
       - Mapper ID: getEmployeeModalList / getEmployeeModalCount
       - 조건:
           · EMP_STATUS = 'ACTIVE'
           · keyword 입력 시 이름 또는 이메일 LIKE 검색
       - 페이징:
           · ROWNUM BETWEEN startRow AND endRow
    ========================================================= */
    @Override
    public List<EmpDto> getEmployeeModalList(Map<String, Object> param) {
        return sqlSession.selectList("ModalMapper.getEmployeeModalList", param);
    }

    @Override
    public int getEmployeeModalCount(Map<String, Object> param) {
        return sqlSession.selectOne("ModalMapper.getEmployeeModalCount", param);
    }

    /* =========================================================
       [직원 선택 모달 끝]
    ========================================================= */

}
