package com.example.gymerp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.example.gymerp.dto.ScheduleDto;

public interface ScheduleService {

	
	//어드민용 1개 추가됨
	Map<String,Object> searchForAdmin(Map<String,Object> q);
	
	
    // 전체 일정 조회
    List<ScheduleDto> getAllSchedules();

    // 단건 조회 (상세보기)
    ScheduleDto getScheduleById(int shNum);

    // 직원별 일정 조회
    List<ScheduleDto> getSchedulesByEmpNum(int empNum);

    // 날짜 범위별 일정 조회
    List<ScheduleDto> getSchedulesByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    // 일정 등록
    int createSchedule(ScheduleDto schedule);

    // 일정 수정
    int updateSchedule(ScheduleDto schedule);
    
    // 일정 삭제
    int deleteSchedule(int shNum);
    
}