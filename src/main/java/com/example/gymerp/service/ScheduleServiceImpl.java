package com.example.gymerp.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.gymerp.dto.PtLogDto;
import com.example.gymerp.dto.PtRegistrationDto;
import com.example.gymerp.dto.ScheduleDto;
import com.example.gymerp.repository.LogDao;
import com.example.gymerp.repository.ScheduleDao;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

	private final ScheduleDao scheduleDao; // 일정테이블 접근
	private final PtRegistrationService ptRegistrationService; // PT 등록 테이블 접근
	private final LogDao logDao; // PT 로그 테이블 접근
	private final SqlSession session; // SqlSessionTemplate 대신 하나로 통일 추천

	// 어드민용 추가 작성
	@Override
	public Map<String, Object> searchForAdmin(Map<String, Object> p) {
		String keyword = String.valueOf(p.getOrDefault("keyword", "")).trim();
		String codeBid = String.valueOf(p.getOrDefault("codeBid", "")).trim();
		int offset = (int) p.getOrDefault("offset", 0);
		int limit = (int) p.getOrDefault("limit", 20);

		Map<String, Object> param = new HashMap<>();
		param.put("keyword", keyword);
		param.put("codeBid", codeBid);
		param.put("offset", offset);
		param.put("limit", limit);

		List<ScheduleDto> list = scheduleDao.selectByFiltersForAdmin(param);
		int total = scheduleDao.countByFiltersForAdmin(param);

		Map<String, Object> res = new HashMap<>();
		res.put("list", list);
		res.put("total", total);
		res.put("page", (offset / Math.max(limit, 1)) + 1);
		res.put("size", limit);
		return res;
	}

	private static Integer optInt(String s) {
		try {
			return s == null ? null : Integer.valueOf(s);
		} catch (Exception e) {
			return null;
		}
	}

	private static int optInt(String s, int d) {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			return d;
		}
	}

	private static String str(String s) {
		return (s == null || s.isBlank()) ? null : s;
	}

	private static Timestamp ts(String s) {
		if (s == null || s.isBlank())
			return null;
		return Timestamp.valueOf(s.replace("T", " ") + (s.length() == 16 ? ":00" : "")); // 'YYYY-MM-DDTHH:mm'도 대응
	}

	// 전체 일정 조회
	@Override
	public List<ScheduleDto> getAllSchedules() {
		return scheduleDao.selectAll();
	}

	// 단건 조회
	@Override
	public ScheduleDto getScheduleById(int shNum) {
		return scheduleDao.selectByShNum(shNum);
	}

	// 직원별 일정 조회
	@Override
	public List<ScheduleDto> getSchedulesByEmpNum(int empNum) {
		return scheduleDao.selectByEmpNum(empNum);
	}

	/** 기간별 일정 조회 */
	@Override
	public List<ScheduleDto> getSchedulesByDateRange(java.time.LocalDateTime startDate,
			java.time.LocalDateTime endDate) {
		return scheduleDao.selectByDateRange(startDate, endDate);
	}

// ----------------------- 추가 -------------------------------------------------------------------------------------------------
	// 일정 등록
	@Override
	@Transactional // ⚙️ 메서드 전체를 하나의 트랜잭션으로 묶는다. //내부에서 예외(런타임)가 터지면 SCHEDULE/REGISTRATION/PT_LOG 모두 롤백된다.
	public int createSchedule(ScheduleDto schedule) {
	    // 0) 방어 코드: 요청 본문 자체가 null이면 즉시 실패
	    if (schedule == null)
	        throw new IllegalArgumentException("요청 데이터가 없습니다.");

	    // 1) (공통) 트레이너 시간 겹침 방지: SCHEDULE에 조건부 INSERT 시도
	    //    - ScheduleMapper.insertIfNoOverlap: 동일 트레이너(EMPNUM)의 기존 일정과
	    //      (new.start < old.end) && (old.start < new.end) 이면 INSERT 하지 않음(0행)
	    //    - 성공 시 shNum은 selectKey로 미리 생성됨
	    int inserted = scheduleDao.insertIfNoOverlap(schedule); // ★ 핵심: DB에서 겹침 정책 강제
	    if (inserted == 0) {
	        // 동일 트레이너에게 겹치는 시간대가 있으므로 등록 거부 → 예외로 트랜잭션 롤백
	        throw new IllegalStateException("해당 트레이너의 같은 시간대에 이미 일정이 있습니다.");
	    }
	    System.out.println("[일정 등록 완료] shNum=" + schedule.getShNum() + ", codeBid=" + schedule.getCodeBid());

	    // 2) 비-PT 일정(VACATION/ETC 등)은 여기서 종료
	    //    - 시간 겹침만 통과하면 SCHEDULE만 등록하면 됨
	    if (!"SCHEDULE-PT".equalsIgnoreCase(schedule.getCodeBid())) {
	        return inserted; // 보통 1
	    }

	    // 3) PT 일정은 반드시 회원 지정이 필요
	    if (schedule.getMemNum() == null) {
	        // 프론트에서 사용자에게 바로 보여줄 에러 메시지
	        throw new IllegalStateException("PT 예약에는 회원(memNum)이 필요합니다.");
	    }

	    // (디버깅/감사 로그용) 현재 회원의 회원권/잔여PT 상태를 먼저 찍어둔다.
	    //   - 운영 정책은 아래 4)에서 DB가 강제하지만, 실패 시 원인 파악을 쉽게 하기 위함
	    Long mem = schedule.getMemNum().longValue();
	    Integer voucherValid = session.selectOne("LogMapper.checkVoucherValid", mem);        // 유효 회원권 여부 (1/0)
	    Integer remainingPt  = session.selectOne("LogMapper.selectRemainingPtCount", mem);   // 잔여 PT 합계(충전-소비 누계)
	    System.out.printf("[사전체크] memNum=%d, voucherValid=%d, remainingPt=%d%n",
	            mem,
	            (voucherValid == null ? 0 : voucherValid),
	            (remainingPt == null ? 0 : remainingPt));

	    // 4) REGISTRATION(PT 예약) 조건부 INSERT
	    //    - PtRegistrationMapper.insertRegistrationIfAllowed:
	    //        a) 유효 회원권 존재(endDate >= SYSDATE 또는 TRUNC(SYSDATE) 기준)
	    //        b) 잔여 PT > 0
	    //        c) 동일 shNum으로 이미 등록된 REGISTRATION 없음
	    //      위 조건을 SQL에서 직접 체크 → 불충족 시 INSERT 0행을 반환
	    PtRegistrationDto reg = PtRegistrationDto.builder()
	            .empNum((long) schedule.getEmpNum())
	            .memNum(mem)
	            .shNum((long) schedule.getShNum())
	            .regNote(schedule.getMemo())
	            .build();

	    int rows = session.insert("PtRegistrationMapper.insertRegistrationIfAllowed", reg);
	    if (rows == 0) {
	        // 실패 원인을 콘솔에 남기고 예외 발생 → @Transactional에 의해 SCHEDULE도 롤백됨
	        System.out.printf("[차단사유] memNum=%d, voucherValid=%d, remainingPt=%d, shNum=%d%n",
	                mem,
	                (voucherValid == null ? 0 : voucherValid),
	                (remainingPt == null ? 0 : remainingPt),
	                schedule.getShNum());
	        throw new IllegalStateException("유효한 회원권이 없거나 잔여 PT 회차가 없거나 이미 등록된 일정입니다.");
	    }
	    System.out.println("[PT 예약 등록 완료] regNum=" + reg.getRegNum());

	    // 5) PT_LOG에 소비(-1) 기록
	    //    - LogMapper.insertPtConsumeLog: '잔여 PT>0'일 때만 실제 INSERT 되도록 SQL에서 강제
	    //    - 동시성(다른 요청이 동시에 소비해서 0이 되는 경우) 대비로 INSERT 결과가 0이면 예외
	    PtLogDto consume = PtLogDto.builder()
	            .memNum(mem)
	            .empNum((long) schedule.getEmpNum())
	            .regId(reg.getRegNum().longValue())
	            .status("소비")      // 도메인 상태값
	            .countChange(-1L)    // 회차 차감
	            .build();

	    System.out.println("[소비로그 시도] memNum=" + mem + ", regId=" + reg.getRegNum());
	    int logRows = session.insert("LogMapper.insertPtConsumeLog", consume);
	    System.out.println("[소비로그 결과] insertedRows=" + logRows);
	    if (logRows == 0) {
	        // 이 경우도 예외 → 트랜잭션 전체 롤백(SCHEDULE/REGISTRATION 포함)
	        throw new IllegalStateException("남은 PT 회차가 없어 예약할 수 없습니다.");
	    }

	    // 6) 여기까지 오면 SCHEDULE/REGISTRATION/PT_LOG(소비) 모두 정상 반영됨
	    return inserted; // 보통 1
	}
// ----------------------- 수정 -------------------------------------------------------------------------------------------------

	// 일정 수정
	@Transactional
	@Override
	public int updateSchedule(ScheduleDto schedule) {
	    if (schedule == null) throw new IllegalArgumentException("요청 데이터가 없습니다.");

	    final boolean isPT = "SCHEDULE-PT".equalsIgnoreCase(schedule.getCodeBid()); // ★ 먼저 선언

	    // PT는 회원 필수
	    if (isPT && schedule.getMemNum() == null) {
	        throw new IllegalStateException("PT 일정은 회원이 필요합니다.");
	    }

	    // 시간 겹침(공통) + (PT이면) 회원권 기간 체크까지
	    int updated = isPT
	        ? scheduleDao.updateIfNoOverlapAndVoucherOK(schedule)  // ★ 새 쿼리
	        : scheduleDao.updateIfNoOverlap(schedule);             // 기존 쿼리

	    if (updated == 0) {
	        // PT는 '겹침 또는 회원권 기간 벗어남' 둘 다 가능성
	        String msg = isPT
	            ? "겹치거나 회원권 기간을 벗어나 수정할 수 없습니다."
	            : "해당 트레이너의 같은 시간대에 이미 다른 일정이 있어 수정할 수 없습니다.";
	        throw new IllegalStateException(msg);
	    }

	    // (선택) PT에서 memNum 변경 시 REGISTRATION/LOG 전환 로직을 여기에 추가 가능
	    // - 시간만 변경이면 추가 처리 없음

	    System.out.println("[일정 수정 완료] shNum=" + schedule.getShNum());
	    return updated;
	}

// ----------------------- 추가 -------------------------------------------------------------------------------------------------

	// 일정 삭제
	@Override
	@Transactional
	public int deleteSchedule(int shNum) {
		// 삭제 대상 조회
		ScheduleDto target = scheduleDao.selectByShNum(shNum);
		if (target == null) {
			throw new IllegalArgumentException("존재하지 않는 일정입니다.");
		}

		System.out.println("[일정 삭제 요청] shNum=" + shNum + ", type=" + target.getCodeBid());

		// PT 일정인 경우만 추가 로직 수행
		if ("SCHEDULE-PT".equalsIgnoreCase(target.getCodeBid())) {
			Integer regNum = ptRegistrationService.findRegNumByShNum(shNum);

			if (regNum != null) {
				// 회원이 존재할 때만 PT_LOG 기록
				if (target.getMemNum() != null) {
					PtLogDto cancelLog = PtLogDto.builder().memNum(target.getMemNum().longValue())
							.empNum((long) target.getEmpNum()).regId(regNum.longValue()).status("예약취소").countChange(1L)
							.build();

					logDao.insertPtCancelLog(cancelLog);
					System.out.println("[PT 예약취소 로그 등록 완료] regNum=" + regNum);
				} else {
					System.out.println("[회원 없는 일정 → PT 로그 생략]");
				}

				// REGISTRATION 테이블에서 PT 등록 삭제
				ptRegistrationService.deletePtRegistration(regNum);
				System.out.println("[PT 등록 데이터 삭제 완료]");
			} else {
				System.out.println("[PT 등록번호 없음 → 로그 등록 생략]");
			}
		}

		// SCHEDULE 테이블에서 일정 삭제
		int deleted = scheduleDao.delete(shNum);
		System.out.println("[일정 삭제 완료] deleted=" + deleted);

		return deleted;
	}

}