//package com.example.gymerp.repository;
//
//import com.example.gymerp.domain.Employee;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * 	[Repository 테스트 목적]
// * - MyBatis 매퍼나 DAO의 SQL이 제대로 동작하는지 확인하는 테스트 클래스.
// * - DB: H2 (테스트 환경용 메모리 DB)
// * - @MybatisTest: MyBatis 관련 빈만 로딩해서 가볍게 실행됨.
// * - @ActiveProfiles("test"): src/test/resources/application-test.properties 설정 사용.
// */
//@MybatisTest
//@ActiveProfiles("test")
//class EmployeeRepositoryTest {
//
//    // 실제 MyBatis 매퍼 객체 주입
//    @Autowired
//    private EmployeeMapper employeeMapper;
//
//    @Test
//    @DisplayName("직원 전체 조회 테스트")
//    void findAllEmployees() {
//        //  when: 직원 목록 전체 조회
//        List<Employee> employees = employeeMapper.findAll();
//
//        //  then: 결과 검증
//        assertNotNull(employees, "직원 리스트가 null이면 안 됨");
//        assertTrue(employees.size() >= 3, "기본 데이터 3개 이상 있어야 함");
//        assertEquals("김구라", employees.get(0).getEmpName(), "첫 번째 직원 이름은 김구라여야 함");
//    }
//
//    @Test
//    @DisplayName("직원 단일 조회 테스트")
//    void findEmployeeById() {
//        //  given: 직원번호 1
//        long empNum = 1L;
//
//        //  when: 직원 단건 조회
//        Employee e = employeeMapper.findById(empNum);
//
//        //  then: 결과 검증
//        assertNotNull(e, "해당 직원이 존재해야 함");
//        assertEquals("김구라", e.getEmpName(), "직원 이름이 김구라인지 확인");
//    }
//}
