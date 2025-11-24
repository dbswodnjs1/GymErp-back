package com.example.gymerp.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.gymerp.dto.ScheduleDto;
import com.example.gymerp.service.ScheduleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    
    //어드민용 추가 2개
    @GetMapping("/schedules/search")
    public Map<String, Object> searchForAdmin(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "codeBid", required = false) String codeBid,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            Authentication auth
    ){
        if (!isAdmin(auth)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ADMIN only");
        }
        // 음수/0 방지
        page = Math.max(1, page);
        size = Math.max(1, size);

        Map<String,Object> p = new HashMap<>();
        p.put("keyword", (keyword == null ? "" : keyword.trim()));
        p.put("codeBid", (codeBid == null ? "" : codeBid.trim()));
        p.put("offset", (page - 1) * size);
        p.put("limit", size);

        return scheduleService.searchForAdmin(p);
    }

    private boolean isAdmin(Authentication auth){
        if (auth == null) return false;
        // 권한이 ROLE_ADMIN 형태라면:
        return auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        // 만약 Employee.role 값을 세션/토큰에서 직접 보신다면 여기를 프로젝트에 맞게 교체.
    }
    
    
    
    
    // 전체일정 조회 
    @GetMapping("/schedule/all")
    public ResponseEntity<List<ScheduleDto>> getAllSchedules() {
        List<ScheduleDto> list = scheduleService.getAllSchedules();
        return ResponseEntity.ok(list);
    }

    // 일정 상세 조회
    @GetMapping("/schedule/{shNum}")
    public ResponseEntity<ScheduleDto> getScheduleById(@PathVariable int shNum) {
        ScheduleDto dto = scheduleService.getScheduleById(shNum);
        return ResponseEntity.ok(dto);
    }

    // 직원별 일정 조회
    @GetMapping("/schedule/emp/{empNum}")
    public ResponseEntity<List<ScheduleDto>> getSchedulesByEmp(@PathVariable int empNum) {
        List<ScheduleDto> list = scheduleService.getSchedulesByEmpNum(empNum);
        return ResponseEntity.ok(list);
    }

   	// 날짜 범위 조회 (달력)
    @GetMapping("/schedule/range")
    public ResponseEntity<List<ScheduleDto>> getSchedulesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        List<ScheduleDto> list = scheduleService.getSchedulesByDateRange(startDate, endDate);
        return ResponseEntity.ok(list);
    }

 //------------------------------------수정-------------------------------------------------------
    // 일정 등록 
    @PostMapping("/schedule/add")
    public ResponseEntity<String> createSchedule(@RequestBody ScheduleDto scheduleDto) {
    	try {
            scheduleService.createSchedule(scheduleDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("일정이 등록되었습니다.");
        } catch (IllegalStateException e) {
            // 회원권 만료, 잔여 PT 0, 중복 등 “비즈니스 조건 위반” → 409 Conflict
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (IllegalArgumentException e) {
            // 파라미터 누락 등 → 400 Bad Request
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
//-------------------------------------------------------------------------------------------------------

    // 일정 수정 
    @PutMapping("/schedule/update")
    public ResponseEntity<String> updateSchedule(@RequestBody ScheduleDto scheduleDto) {
        scheduleService.updateSchedule(scheduleDto);
        return ResponseEntity.ok("일정이 수정되었습니다.");
    }
    
    // 일정 삭제 
    @DeleteMapping("/schedule/delete/{shNum}")
    public ResponseEntity<String> deleteSchedule(@PathVariable int shNum) {
        scheduleService.deleteSchedule(shNum);
        return ResponseEntity.ok("일정이 삭제되었습니다.");
    }

}