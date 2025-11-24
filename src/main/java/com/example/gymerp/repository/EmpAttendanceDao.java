package com.example.gymerp.repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.example.gymerp.dto.EmpAttendanceDto;

public interface EmpAttendanceDao {

	List<EmpAttendanceDto> selectAllByDate(@Param("date") Date date);
    // 전체 근태 목록
    List<EmpAttendanceDto> selectAllEmpAttendances();

    // 근태 단건 조회
    EmpAttendanceDto selectEmpAttendanceById(@Param("attNum") int attNum);

    // 직원별 근태 목록
    List<EmpAttendanceDto> selectEmpAttendancesByEmpNum(@Param("empNum") int empNum);

    // ✅ 기간(달력 범위) 조회
    List<EmpAttendanceDto> selectEmpAttendancesByRange(
            @Param("empNum") int empNum,
            @Param("from") Date from,
            @Param("to") Date to
    );

    // 출근(등록)
    int insertEmpAttendance(EmpAttendanceDto dto);

    // 퇴근 시간 업데이트
    int updateEmpAttendanceCheckOut(@Param("attNum") int attNum, @Param("checkOut") Timestamp checkOut);

    // 전체 수정
    int updateEmpAttendance(EmpAttendanceDto dto);

    // 삭제
    int deleteEmpAttendance(@Param("attNum") int attNum);
    
    //출근용 추가
    List<EmpAttendanceDto> selectAllByRange(
    	    @Param("from") Date from,
    	    @Param("to")   Date to
    	);
}
