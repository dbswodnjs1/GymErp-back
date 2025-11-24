package com.example.gymerp.repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import com.example.gymerp.dto.EmpAttendanceDto;

import lombok.RequiredArgsConstructor;

@Primary
@Repository
@RequiredArgsConstructor
public class EmpAttendanceDaoImpl implements EmpAttendanceDao {
    private final SqlSessionTemplate session;

    @Override
    public List<EmpAttendanceDto> selectAllByDate(Date date) {
        return session.selectList("EmpAttendanceMapper.selectAllByDate", date);
    }
    
    @Override
    public List<EmpAttendanceDto> selectEmpAttendancesByEmpNum(int empNum) {
        return session.selectList("EmpAttendanceMapper.selectEmpAttendancesByEmpNum", empNum); // ✅
    }

    @Override
    public EmpAttendanceDto selectEmpAttendanceById(int attNum) {
        return session.selectOne("EmpAttendanceMapper.	selectEmpAttendanceById", attNum); // ✅
    }

    @Override
    public List<EmpAttendanceDto> selectAllEmpAttendances() {
        return session.selectList("EmpAttendanceMapper.selectAllEmpAttendances");
    }

    @Override
    public int insertEmpAttendance(EmpAttendanceDto dto) {
        return session.insert("EmpAttendanceMapper.insertEmpAttendance", dto); // ✅
    }

    @Override
    public int updateEmpAttendanceCheckOut(int attNum, Timestamp checkOut) {
        Map<String,Object> p = new HashMap<>();
        p.put("attNum", attNum);
        p.put("checkOut", checkOut);
        return session.update("EmpAttendanceMapper.updateEmpAttendanceCheckOut", p); // ✅
    }

    @Override
    public int updateEmpAttendance(EmpAttendanceDto dto) {
        return session.update("EmpAttendanceMapper.updateEmpAttendance", dto); // ✅
    }

    @Override
    public int deleteEmpAttendance(int attNum) {
        return session.delete("EmpAttendanceMapper.deleteEmpAttendance", attNum); // ✅
    }
//
    @Override
    public List<EmpAttendanceDto> selectEmpAttendancesByRange(int empNum, Date from, Date to) {
        Map<String,Object> p = Map.of("empNum", empNum, "from", from, "to", to);
        return session.selectList("EmpAttendanceMapper.selectEmpAttendancesByRange", p); // ✅
    }
       // ✅ 전직원 기간조회 (ServiceImpl에서 empNum<=0일 때 호출)
       @Override
       public List<EmpAttendanceDto> selectAllByRange(Date from, Date to) {
           Map<String,Object> p = Map.of("from", from, "to", to);
           return session.selectList("EmpAttendanceMapper.selectAllByRange", p);
       }
}
