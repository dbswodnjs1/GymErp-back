//package com.example.gymerp.controller;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyBoolean;
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import java.math.BigDecimal;
//import java.util.Collections;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
//import org.mybatis.spring.boot.autoconfigure.MybatisLanguageDriverAutoConfiguration;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Repository;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.FilterType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import com.example.gymerp.dto.ServiceDto;
//import com.example.gymerp.dto.ServiceListResponse;
//import com.example.gymerp.service.ServiceService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//@WebMvcTest(
//    controllers = ServiceController.class,
//    excludeFilters = {
//        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class),
//        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = org.apache.ibatis.annotations.Mapper.class)
//    },
//    excludeAutoConfiguration = {
//        MybatisAutoConfiguration.class,
//        MybatisLanguageDriverAutoConfiguration.class
//    }
//)
//@AutoConfigureMockMvc(addFilters = false)
//class ServiceControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private ServiceService serviceService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    @DisplayName("서비스 목록 조회 API 테스트 - 성공")
//    void getServiceList_Success() throws Exception {
//        // given
//        ServiceListResponse fakeResponse = ServiceListResponse.builder()
//                .list(Collections.singletonList(new ServiceDto()))
//                .totalRow(1)
//                .totalPageCount(1)
//                .pageNum(1)
//                .build();
//        when(serviceService.getServices(anyInt(), any(ServiceDto.class))).thenReturn(fakeResponse);
//
//        // when & then
//        mockMvc.perform(get("/v1/service")
//                .with(user("testuser"))
//                .param("pageNum", "1")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.totalRow").value(1))
//            .andDo(print());
//    }
//
//    @Test
//    @DisplayName("서비스 상세 조회 API 테스트 - 성공")
//    void getServiceDetail_Success() throws Exception {
//        // given
//        int serviceId = 1;
//        ServiceDto fakeService = ServiceDto.builder()
//                .serviceId(serviceId)
//                .name("테스트 서비스")
//                .price(BigDecimal.valueOf(10000))
//                .build();
//        when(serviceService.getDetail(serviceId)).thenReturn(fakeService);
//
//        // when & then
//        mockMvc.perform(get("/v1/service/{serviceId}", serviceId)
//                .with(user("testuser")))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.name").value("테스트 서비스"))
//            .andDo(print());
//    }
//
//    @Test
//    @DisplayName("신규 서비스 등록 API 테스트 - 성공")
//    void createService_Success() throws Exception {
//        // given
//        ServiceDto newService = ServiceDto.builder()
//                .name("새로운 서비스")
//                .price(BigDecimal.valueOf(25000))
//                .build();
//        doNothing().when(serviceService).save(any(ServiceDto.class));
//
//        // when & then
//        mockMvc.perform(post("/v1/service")
//                .with(user("testuser"))
//                .with(csrf())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(newService)))
//            .andExpect(status().isOk())
//            .andDo(print());
//
//        verify(serviceService, times(1)).save(any(ServiceDto.class));
//    }
//
//    @Test
//    @DisplayName("서비스 정보 수정 API 테스트 - 성공")
//    void updateService_Success() throws Exception {
//        // given
//        int serviceId = 1;
//        ServiceDto updatedService = ServiceDto.builder()
//                .name("수정된 서비스")
//                .price(BigDecimal.valueOf(12000))
//                .build();
//        doNothing().when(serviceService).modifyService(any(ServiceDto.class));
//
//        // when & then
//        mockMvc.perform(put("/v1/service/{serviceId}", serviceId)
//                .with(user("testuser"))
//                .with(csrf())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(updatedService)))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.name").value("수정된 서비스"))
//            .andDo(print());
//
//        verify(serviceService, times(1)).modifyService(any(ServiceDto.class));
//    }
//
//    @Test
//    @DisplayName("서비스 판매 상태 변경 API 테스트 - 성공")
//    void updateServiceStatus_Success() throws Exception {
//        // given
//        int serviceId = 1;
//        boolean newStatus = false; // 판매 중지 상태로 변경
//        ServiceDto statusUpdateDto = ServiceDto.builder().isActive(newStatus).build();
//        doNothing().when(serviceService).updateServiceStatus(anyInt(), anyBoolean());
//
//        // when & then
//        mockMvc.perform(patch("/v1/service/{serviceId}", serviceId)
//                .with(user("testuser"))
//                .with(csrf())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(statusUpdateDto)))
//            .andExpect(status().isOk())
//            .andDo(print());
//
//        verify(serviceService, times(1)).updateServiceStatus(eq(serviceId), eq(newStatus));
//    }
//}
