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
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import com.example.gymerp.dto.ProductDto;
//import com.example.gymerp.dto.ProductListResponse;
//import com.example.gymerp.service.ProductService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//// ProductController만 테스트하기 위한 설정
//@WebMvcTest(ProductController.class)
//class ProductAControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc; // API를 가상으로 호출하기 위한 객체
//
//    @MockBean
//    private ProductService productService; // 가짜 ProductService 객체
//
//    @Autowired
//    private ObjectMapper objectMapper; // 객체를 JSON으로 변환하기 위한 객체
//
//    @Test
//    @DisplayName("상품 목록 조회 API 테스트 - 성공")
//    void getProductList_Success() throws Exception {
//        // given
//        ProductListResponse fakeResponse = ProductListResponse.builder()
//                .list(Collections.singletonList(new ProductDto()))
//                .totalRow(1)
//                .totalPageCount(1)
//                .pageNum(1)
//                .build();
//        when(productService.getProducts(anyInt(), any(ProductDto.class))).thenReturn(fakeResponse);
//
//        // when & then
//        mockMvc.perform(get("/v1/product")
//                .with(user("testuser"))
//                .param("pageNum", "1")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.totalRow").value(1))
//            .andDo(print());
//    }
//
//    @Test
//    @DisplayName("상품 상세 조회 API 테스트 - 성공")
//    void getProductDetail_Success() throws Exception {
//        // given
//        int productId = 1;
//        ProductDto fakeProduct = ProductDto.builder()
//                .productId(productId)
//                .name("테스트 상품")
//                .price(BigDecimal.valueOf(10000))
//                .build();
//        when(productService.getDetail(productId)).thenReturn(fakeProduct);
//
//        // when & then
//        mockMvc.perform(get("/v1/product/{productId}", productId)
//                .with(user("testuser")))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.name").value("테스트 상품"))
//            .andDo(print());
//    }
//
////    @Test
////    @DisplayName("신규 상품 등록 API 테스트 - 성공")
////    void createProduct_Success() throws Exception {
////        // given
////        ProductDto newProduct = ProductDto.builder()
////                .name("새로운 상품")
////                .price(BigDecimal.valueOf(25000))
////                .build();
////        doNothing().when(productService).save(any(ProductDto.class));
////
////        // when & then
////        mockMvc.perform(post("/v1/product")
////                .with(user("testuser"))
////                .with(csrf()) // CSRF 토큰 추가
////                .contentType(MediaType.APPLICATION_JSON)
////                .content(objectMapper.writeValueAsString(newProduct)))
////            .andExpect(status().isOk())
////            .andDo(print());
////
////        verify(productService, times(1)).save(any(ProductDto.class));
////    }
////
////    @Test
////    @DisplayName("상품 정보 수정 API 테스트 - 성공")
////    void updateProduct_Success() throws Exception {
////        // given
////        int productId = 1;
////        ProductDto updatedProduct = ProductDto.builder()
////                .name("수정된 상품")
////                .price(BigDecimal.valueOf(12000))
////                .build();
////        doNothing().when(productService).modifyProduct(any(ProductDto.class));
////
////        // when & then
////        mockMvc.perform(put("/v1/product/{productId}", productId)
////                .with(user("testuser"))
////                .with(csrf()) // CSRF 토큰 추가
////                .contentType(MediaType.APPLICATION_JSON)
////                .content(objectMapper.writeValueAsString(updatedProduct)))
////            .andExpect(status().isOk())
////            .andExpect(jsonPath("$.name").value("수정된 상품"))
////            .andDo(print());
////
////        verify(productService, times(1)).modifyProduct(any(ProductDto.class));
////    }
//
//    @Test
//    @DisplayName("상품 판매 상태 변경 API 테스트 - 성공")
//    void updateProductStatus_Success() throws Exception {
//        // given
//        int productId = 1;
//        boolean newStatus = false; // 판매 중지 상태로 변경
//        ProductDto statusUpdateDto = ProductDto.builder().isActive(newStatus).build();
//        doNothing().when(productService).updateProductStatus(anyInt(), anyBoolean());
//
//        // when & then
//        mockMvc.perform(patch("/v1/product/{productId}", productId)
//                .with(user("testuser"))
//                .with(csrf()) // CSRF 토큰 추가
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(statusUpdateDto)))
//            .andExpect(status().isOk())
//            .andDo(print());
//
//        verify(productService, times(1)).updateProductStatus(eq(productId), eq(newStatus));
//    }
//}
