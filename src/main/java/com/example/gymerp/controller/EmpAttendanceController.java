// src/main/java/com/example/gymerp/controller/EmpAttendanceController.java
package com.example.gymerp.controller;

import java.net.URI;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;                  // ✅ 추가
import java.time.ZonedDateTime;          // ✅ 추가
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.gymerp.dto.EmpAttendanceDto;
import com.example.gymerp.service.EmpAttendanceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1") // 프론트는 /api/v1 → 프록시로 /v1 전달
@RequiredArgsConstructor
@Slf4j
public class EmpAttendanceController {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul"); // ✅ KST 고정
    private final EmpAttendanceService service;

    /* ======================= 조회 ======================= */

    // 전직원 하루치: /v1/attendance?date=YYYY-MM-DD
    @GetMapping(value = "/attendance", params = "date")
    public List<EmpAttendanceDto> getDaily(@RequestParam String date) {
        return service.getAllByDate(Date.valueOf(LocalDate.parse(date)));
    }

    // 범위 조회(전직원/특정직원)
    // /v1/attendance?from=YYYY-MM-DD&to=YYYY-MM-DD
    // /v1/attendance?from=YYYY-MM-DD&to=YYYY-MM-DD&empNum=83
    @GetMapping(value = "/attendance", params = {"from","to"})
    public List<EmpAttendanceDto> getByRange(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(required = false) Integer empNum
    ) {
        Date df = Date.valueOf(LocalDate.parse(from));
        Date dt = Date.valueOf(LocalDate.parse(to));

        if (empNum == null) {
            // 단일일자면 하루치로
            if (from.equals(to)) return service.getAllByDate(df);
            // 전직원 기간 조회는 empNum=0 규약으로 서비스에서 처리하도록 위임
            return service.getEmpAttendancesByRange(0, df, dt);
        }
        return service.getEmpAttendancesByRange(empNum, df, dt);
    }

    // 백오피스용 전체
    @GetMapping("/attendance")
    public List<EmpAttendanceDto> getAll() {
        return service.getAllEmpAttendances();
    }

    // 직원별 전체: /v1/attendance?empNum=83
    @GetMapping(value = "/attendance", params = "empNum")
    public List<EmpAttendanceDto> getByEmp(@RequestParam int empNum) {
        return service.getEmpAttendancesByEmpNum(empNum);
    }

    // 단건
    @GetMapping("/attendance/{attNum}")
    public EmpAttendanceDto getOne(@PathVariable int attNum) {
        return service.getEmpAttendanceById(attNum);
    }

    /* ======================= 쓰기 ======================= */

    // 출근(등록) — 바디 무시, 로그인에서 empNum만 사용(필요 시 헤더 임시 fallback)
    @PostMapping("/attendance")
    public ResponseEntity<Void> checkIn(
            Authentication auth,
            @RequestHeader(value = "X-EMP-NUM", required = false) Integer empNumHeader
    ) {
        Integer empNum = null;

        // 커스텀 UserDetails에 getEmpNum() 있다고 가정 (프로젝트 클래스명에 맞게 캐스팅/리플렉션)
        try {
            if (auth != null && auth.getPrincipal() != null) {
                Object p = auth.getPrincipal();
                var m = p.getClass().getMethod("getEmpNum");
                Object v = m.invoke(p);
                if (v != null) empNum = (v instanceof Number) ? ((Number) v).intValue() : Integer.parseInt(v.toString());
            }
        } catch (Exception ignore) { }

        // 로그인 연동 전 테스트용 임시 헤더
        if (empNum == null && empNumHeader != null && empNumHeader > 0) {
            empNum = empNumHeader;
        }

        if (empNum == null || empNum <= 0) {
            throw new IllegalArgumentException("empNum (from auth) is required");
        }

        // ✅ DB 세션 타임존 영향 제거: KST '지금'을 서버에서 확정 생성해 전달
        Timestamp nowKst = Timestamp.from(ZonedDateTime.now(KST).toInstant());
        service.checkIn(empNum, nowKst); // ✅ 서비스 시그니처: checkIn(int empNum, Timestamp now) 로 변경 필요

        log.info("checkIn OK empNum={} at {}", empNum, nowKst);
        return ResponseEntity.created(URI.create("/v1/attendance")).build();
    }

    // 퇴근 — 파라미터 없으면 KST 지금, 주어지면 KST로 해석해 저장
    @PutMapping("/attendance/{attNum}/checkout")
    public ResponseEntity<Void> checkOut(
            @PathVariable int attNum,
            @RequestParam(required = false) String checkOut
    ) {
        // ✅ DB SYSTIMESTAMP를 쓰지 않고, 항상 애플리케이션에서 KST 타임스탬프 생성
        Timestamp ts;
        if (checkOut == null || checkOut.isBlank()) {
            ts = Timestamp.from(ZonedDateTime.now(KST).toInstant()); // KST 현재
        } else {
            // 프론트가 tz 없는 'YYYY-MM-DDTHH:mm[:ss]'를 보낸다고 가정 → KST로 해석
            LocalDateTime ldt = LocalDateTime.parse(checkOut, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            ts = Timestamp.from(ldt.atZone(KST).toInstant());
        }
        service.updateEmpAttendanceCheckOut(attNum, ts);
        return ResponseEntity.noContent().build();
    }

    // 전체 수정
    @PutMapping("/attendance/{attNum}")
    public ResponseEntity<Void> update(@PathVariable int attNum, @RequestBody EmpAttendanceDto dto) {
        dto.setAttNum(attNum);
        service.updateEmpAttendance(dto);
        return ResponseEntity.noContent().build();
    }

    // 삭제
    @DeleteMapping("/attendance/{attNum}")
    public ResponseEntity<Void> delete(@PathVariable int attNum) {
        service.deleteEmpAttendance(attNum);
        return ResponseEntity.noContent().build();
    }
}
