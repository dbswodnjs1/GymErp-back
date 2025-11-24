package com.example.gymerp.repository;

import org.apache.ibatis.annotations.Mapper;
import com.example.gymerp.dto.PtRegistrationDto;
import java.util.List;

@Mapper
public interface PtRegistrationMapper {
    int insertPtRegistration(PtRegistrationDto dto);
    List<PtRegistrationDto> getAllPtRegistration();
    PtRegistrationDto getPtRegistrationById(int regNum);
    PtRegistrationDto getRegistrationByShNum(int shNum);
    int deletePtRegistration(Long regNum); 
}
