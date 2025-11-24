package com.example.gymerp.repository;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import com.example.gymerp.dto.ServiceDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
@Primary
public class ServiceDaoImpl implements ServiceDao{

	//의존 객체
	private final SqlSession session;
	
	@Override
	public List<ServiceDto> selectPage(ServiceDto dto) {
		
		return session.selectList("service.selectPage", dto);
	}

	@Override
	public int getCount(ServiceDto dto) {
		
		return session.selectOne("service.getCount", dto);
	}

	@Override
	public void insert(ServiceDto dto) {
		session.insert("service.insert", dto);
	}

	@Override
	public int update(ServiceDto dto) {
		
		return session.update("service.update", dto);
	}

	@Override
	public ServiceDto getByNum(int serviceId) {
		
		return session.selectOne("service.getByNum", serviceId);
	}

	@Override
	public int updateServiceStatus(ServiceDto dto) {
		
		return session.update("service.updateStatus", dto);
	}

	
}
