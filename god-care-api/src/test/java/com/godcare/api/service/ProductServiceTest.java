package com.godcare.api.service;


import com.godcare.api.entity.Product;
import com.godcare.api.repository.ProductRepository;
import com.godcare.common.dto.ResisterProductRequest;
import com.godcare.common.dto.ViewProductResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@SpringBootTest
@Slf4j
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @DisplayName("상품을 등록한다")
    @Test
    void viewProduct(){
        // given: 상품등록위한 준비과정

        ResisterProductRequest request = new ResisterProductRequest("vitamin.img", 3L);

        // when: 실제로 상품을 저장
        Product savedProduct = productService.addProduct(Product.toProduct(request));

        // then: 상품이 잘 저장되었는지 검증
        final ViewProductResponse response = productService.getProduct(savedProduct.getId());
        assertEquals(savedProduct.getId(), response.getProductId());

        // verify: 특정 메소드가 호출된 횟수를 추가로 검증
//        verify(productRepository, times(1)).save(any(Product.class));
    }
}