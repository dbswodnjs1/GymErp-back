package com.example.gymerp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.gymerp.dto.ScheduleDto;

@Mapper
public interface ScheduleDao {

	/*하단 2개 어드민용 추가됨 */
	List<ScheduleDto> selectByFiltersForAdmin(Map<String,Object> p);
    int countByFiltersForAdmin(Map<String,Object> p);
	
	
    /* 전체 일정 조회 (관리자용) */
    public List<ScheduleDto> selectAll();

    /* 특정 일정 상세조회 */
    public ScheduleDto selectByShNum(@Param("shNum") int shNum);

    /* 직원별 일정 조회 */
    public List<ScheduleDto> selectByEmpNum(@Param("empNum") int empNum);

    /* 날짜 범위별 조회 (월간 캘린더) */
    public List<ScheduleDto> selectByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /* 일정 등록 */
    public int insert(ScheduleDto schedule);

    /* 일정 수정 */
    public int update(ScheduleDto schedule);

    /* 일정 삭제 */
    public int delete(@Param("shNum") int shNum);


    // 특정 직원의 휴가가 지정 구간과 겹치는지 카운트
    int countVacationOverlap(long empNum, LocalDateTime startTime, LocalDateTime endTime);
    
    // 자기자신 shNum 을 제외하고 겹치는지 카운트 
    int countVacationOverlapExcludingSelf(long empNum, LocalDateTime startTime, LocalDateTime endTime, long shNum);


 // ----------------------- 추가 -------------------------

    int insertIfNoOverlap(ScheduleDto dto);     

    int updateIfNoOverlap(ScheduleDto schedule);
    
 // ----------------------- 추가 -------------------------
    int updateIfNoOverlapAndVoucherOK(ScheduleDto schedule);
//--------------------------------------------------------
}