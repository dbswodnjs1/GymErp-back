package com.example.gymerp.service;

//import com.example.gymerp.domain.Member;
//import com.example.gymerp.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.gymerp.dto.SalesService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 *  [Service 테스트 목적]
 * - 비즈니스 로직 단위 테스트 (회원 등록, 조회 등)
 * - 실제 DB와 연결됨 (테스트용 H2)
 * - @SpringBootTest: 전체 컨텍스트 로딩 (Service, Repository 포함)
 * - @Transactional: 테스트 종료 후 자동 롤백 (DB 깨끗하게 유지)
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SamPleServiceTest {
//
//    @Autowired
//    private MemberService memberService; // 실제 서비스 로직
//
//    @Autowired
//    private MemberRepository memberRepository; // DB 접근 객체
//
//    @Test
//    @DisplayName("회원 등록 테스트")
//    void registerMember() {
//        // given: 신규 회원 정보 준비
//        Member m = new Member();
//        m.setMemName("테스트회원");
//        m.setMemGender("남");
//        m.setMemPhone("010-0000-1111");
//        m.setMemEmail("test@test.com");
//
//        // when: 회원 등록 서비스 호출
//        memberService.registerMember(m);
//
//        // then: DB에 회원이 잘 추가되었는지 확인
//        List<Member> members = memberRepository.findAll();
//        assertTrue(members.stream()
//                .anyMatch(mem -> mem.getMemEmail().equals("test@test.com")),
//                "새로운 회원이 추가되어야 함");
//    }
//
//    @Test
//    @DisplayName("회원 전체 조회 테스트")
//    void getAllMembers() {
//        // when: 전체 회원 목록 조회
//        List<Member> list = memberService.findAll();
//
//        // 🧪 then
//        assertTrue(list.size() >= 3, "기본 데이터 3명 이상 있어야 함");
//    }
}
