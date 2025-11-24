package com.example.gymerp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import com.example.gymerp.dto.ProductDto;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Do not replace the configured DataSource
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD) // 각 테스트 메서드가 끝날 때마다 DB 초기화
@ActiveProfiles("test")
@Import(ProductDaoImpl.class)
public class ProductDaoTest {

    @Autowired
    private ProductDao productDao;

    // Helper method to create a test product DTO
    private ProductDto createProductDto(String name, String codeBId, int price) {
        return ProductDto.builder()
                .name(name)
                .codeBId(codeBId) // Assuming 'B0101', 'B0102' exist in test data
                .price(new BigDecimal(price))
                .isActive(true)
                .note("test note")
                .profileImage("teststring")
                .build();
    }

    @Test
    @DisplayName("상품을 등록하고 ID로 조회할 수 있다")
    void insertAndGetByNum() {
        // Given
        ProductDto newProduct = createProductDto("테스트 상품", "SUPPLEMENT", 15000);

        // When
        productDao.insert(newProduct);
        
        // Then
        assertThat(newProduct.getProductId()).isGreaterThan(0); // Check if ID is generated

        ProductDto foundProduct = productDao.getByNum(newProduct.getProductId());
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getName()).isEqualTo("테스트 상품");
        assertThat(foundProduct.getCodeBId()).isEqualTo("SUPPLEMENT");
        assertThat(foundProduct.getPrice()).isEqualByComparingTo(new BigDecimal("15000"));
    }

    @Test
    @DisplayName("상품 정보를 수정할 수 있다")
    void updateProduct() {
        // Given: Insert a product first
        ProductDto originalProduct = createProductDto("원본 상품", "SUPPLEMENT", 20000);
        productDao.insert(originalProduct);

        // When: Update the product's details
        ProductDto updatedInfo = ProductDto.builder()
                .productId(originalProduct.getProductId())
                .name("수정된 상품")
                .codeBId("EQUIP") // Change category
                .price(new BigDecimal("25000"))
                .isActive(false)
                .note("Updated Note")
                .build();
        int updatedRows = productDao.update(updatedInfo);

        // Then
        assertThat(updatedRows).isEqualTo(1);
        ProductDto fetchedProduct = productDao.getByNum(originalProduct.getProductId());
        assertThat(fetchedProduct.getName()).isEqualTo("수정된 상품");
        assertThat(fetchedProduct.getCodeBId()).isEqualTo("EQUIP");
        assertThat(fetchedProduct.getPrice()).isEqualByComparingTo(new BigDecimal("25000"));
        assertThat(fetchedProduct.getIsActive()).isFalse();
        assertThat(fetchedProduct.getNote()).isEqualTo("Updated Note");
    }

    @Test
    @DisplayName("상품 판매 상태를 변경할 수 있다")
    void updateProductStatus() {
        // Given
        ProductDto product = createProductDto("상태 변경 상품", "EQUIP", 10000);
        product.setIsActive(true);
        productDao.insert(product);

        // When
        ProductDto statusUpdateDto = ProductDto.builder()
                .productId(product.getProductId())
                .isActive(false)
                .build();
        int updatedRows = productDao.updateProductStatus(statusUpdateDto);

        // Then
        assertThat(updatedRows).isEqualTo(1);
        ProductDto result = productDao.getByNum(product.getProductId());
        assertThat(result.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("전체 상품 수와 목록을 페이지네이션 없이 조회한다")
    void selectPageAndGetCount_noFilter() {
        // Given
        productDao.insert(createProductDto("상품 A", "EQUIP", 100));
        productDao.insert(createProductDto("상품 B", "PT_BASIC", 200));

        // When
        ProductDto searchDto = new ProductDto();
        searchDto.setStartRowNum(1);
        searchDto.setEndRowNum(10);
        int count = productDao.getCount(searchDto);
        List<ProductDto> products = productDao.selectPage(searchDto);

        // Then
        assertThat(count).isGreaterThanOrEqualTo(2);
        assertThat(products.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("키워드로 상품을 검색할 수 있다")
    void selectPage_withKeyword() {
        // Given
        productDao.insert(createProductDto("특별한 상품", "EQUIP", 100));
        productDao.insert(createProductDto("일반 상품", "EQUIP", 200));

        // When
        ProductDto searchDto = new ProductDto();
        searchDto.setKeyword("특별한");
        searchDto.setStartRowNum(1);
        searchDto.setEndRowNum(10);
        int count = productDao.getCount(searchDto);
        List<ProductDto> products = productDao.selectPage(searchDto);

        // Then
        assertThat(count).isEqualTo(1);
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("특별한 상품");
    }

    @Test
    @DisplayName("카테고리 코드로 상품을 필터링할 수 있다")
    void selectPage_withCategory() {
        // Given
        productDao.insert(createProductDto("상품 A", "EQUIP", 100));
        productDao.insert(createProductDto("상품 B", "EQUIP", 200));
        productDao.insert(createProductDto("상품 C", "SUPPLEMENT", 300));

        // When
        ProductDto searchDto = new ProductDto();
        searchDto.setCategoryCodes(Arrays.asList("EQUIP"));
        searchDto.setStartRowNum(1);
        searchDto.setEndRowNum(10);
        int count = productDao.getCount(searchDto);
        List<ProductDto> products = productDao.selectPage(searchDto);

        // ThenX
        assertThat(count).isEqualTo(4);
        assertThat(products).hasSize(4); // 테스트 sql 문에서 넣은 상품 2개 포함해서 4개
        assertThat(products).extracting(ProductDto::getCodeBId).containsOnly("EQUIP");
    }
    
    @Test
    @DisplayName("존재하지 않는 상품 조회 시 null을 반환한다")
    void getByNum_nonExisting() {
        // When
        ProductDto foundProduct = productDao.getByNum(Integer.MAX_VALUE);

        // Then
        assertThat(foundProduct).isNull();
    }
}
