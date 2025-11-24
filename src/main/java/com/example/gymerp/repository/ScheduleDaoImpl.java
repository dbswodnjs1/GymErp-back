package com.example.gymerp.repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import com.example.gymerp.dto.ScheduleDto;

import lombok.RequiredArgsConstructor;

@Repository
@Primary
@RequiredArgsConstructor
public class ScheduleDaoImpl implements ScheduleDao {

	private final SqlSession session;

	/* 하단 2개 어드민용 추가됨 */
	@Override
	public List<ScheduleDto> selectByFiltersForAdmin(Map<String, Object> p) {
		return session.selectList("ScheduleMapper.selectByFiltersForAdmin", p);
	}

	@Override
	public int countByFiltersForAdmin(Map<String, Object> p) {
		return session.selectOne("ScheduleMapper.countByFiltersForAdmin", p);
	}

	/* 전체 일정 조회 */
	@Override
	public List<ScheduleDto> selectAll() {
		return session.selectList("ScheduleMapper.selectAll");
	}

	/* 특정 일정 상세조회 */
	@Override
	public ScheduleDto selectByShNum(int shNum) {
		return session.selectOne("ScheduleMapper.selectByShNum", shNum);
	}

	/* 직원별 일정 조회 */
	@Override
	public List<ScheduleDto> selectByEmpNum(int empNum) {
		return session.selectList("ScheduleMapper.selectByEmpNum", empNum);
	}

	/* 날짜 범위 조회 */
	@Override
	public List<ScheduleDto> selectByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
		Map<String, Object> param = new HashMap<>();
		param.put("startDate", startDate);
		param.put("endDate", endDate);
		return session.selectList("ScheduleMapper.selectByDateRange", param);
	}

	/* 일정 등록 */
	@Override
	public int insert(ScheduleDto schedule) {
		return session.insert("ScheduleMapper.insert", schedule);
	}

	/* 일정 수정 */
	@Override
	public int update(ScheduleDto schedule) {
		return session.update("ScheduleMapper.update", schedule);
	}

	/* 일정 삭제 */
	@Override
	public int delete(int shNum) {
		return session.delete("ScheduleMapper.delete", shNum);
	}
	
	

	// ★ 겹침 방지 INSERT 호출
	@Override
	public int insertIfNoOverlap(ScheduleDto dto) {
		return session.insert("ScheduleMapper.insertIfNoOverlap", dto);
	}
	
	
	// ★ 겹침 방지 UPDATE 호출
	@Override
	public int updateIfNoOverlap(ScheduleDto schedule) {
	    return session.update("ScheduleMapper.updateIfNoOverlap", schedule);
	}

	
	
// ----------------------- 추가 ---------------------------------------------
	// (PT일 경우) 시간 겹침 + 회원권 기간 체크까지 통과해야 UPDATE 됨.
	 public int updateIfNoOverlapAndVoucherOK(ScheduleDto schedule) {
	        return session.update("ScheduleMapper.updateIfNoOverlapAndVoucherOK", schedule);
	    }
//---------------------------------------------------------------------------


    @Override
    public int countVacationOverlap(long empNum, LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> param = new HashMap<>();
        param.put("empNum", empNum);
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        return session.selectOne("ScheduleMapper.countVacationOverlap", param);
    }

    @Override
    public int countVacationOverlapExcludingSelf(long empNum, LocalDateTime startTime, LocalDateTime endTime, long shNum) {
        Map<String, Object> param = new HashMap<>();
        param.put("empNum", empNum);
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        param.put("shNum", shNum);
        return session.selectOne("ScheduleMapper.countVacationOverlapExcludingSelf", param);
    }
    
}