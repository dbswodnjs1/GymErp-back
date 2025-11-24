// src/main/java/com/example/gymerp/dao/impl/DashboardDaoImpl.java
package com.example.gymerp.repository;

import com.example.gymerp.dto.DashboardKpiDto;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DashboardDaoImpl implements DashboardDao {

    private final SqlSessionTemplate sqlSession;
    private static final String NS = "com.example.gymerp.repository.DashboardDao";
	@Override
	public DashboardKpiDto selectKpis() {
		return sqlSession.selectOne(NS + ".selectKpis");
	}

}
