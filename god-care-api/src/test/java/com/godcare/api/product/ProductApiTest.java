package com.godcare.api.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.godcare.api.controller.ProductController;
import com.godcare.common.dto.ResisterProductRequest;
import com.godcare.core.service.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.HashMap;
import java.util.Map;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductApiTest {
        Map<Long, Product> persistence = new HashMap<>();
        Long sequence = 0L;

        @Autowired
        MockMvc mvc;
        @Test
        @DisplayName("상품 등록 테스트")
        void uploadProductTest() throws Exception {
            // ResisterProductRequest -> Product 변환
            ResisterProductRequest request = new ResisterProductRequest("main.img", 1L);
            Product productToSave = Product.toProduct(request);
            // Product 저장 로직 구현
            Product savedProduct = save(productToSave);
            // 해당 url로 post 요청
            mvc.perform(post("/api/v1/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(request)))
                    .andExpect(status().isCreated()) // CREATED 상태 코드 예상
                    .andExpect(jsonPath("$.message").value("상품이 등록되었습니다."))
                    .andExpect(jsonPath("$.data").value("productId: " + savedProduct.getId()));
        }
    private Product save(Product product) {
        product.assignId(++sequence);
        persistence.put(product.getId(), product);
        return product;
    }
}
