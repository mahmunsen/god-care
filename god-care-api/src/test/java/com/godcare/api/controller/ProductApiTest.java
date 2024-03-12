package com.godcare.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.godcare.common.dto.ResisterProductRequest;
import com.godcare.api.entity.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(ProductController.class)
public class ProductApiTest {
    Map<Long, Product> persistence = new HashMap<>();
    Long sequence = 0L;

    @Autowired
    MockMvc mvc;

    @Autowired
    private WebApplicationContext ctx;

    @BeforeEach
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }
    @Test
    @DisplayName("상품 등록 테스트")
    void uploadProductTest() throws Exception {
        // ResisterProductRequest -> Product 변환
        ResisterProductRequest request = new ResisterProductRequest("main.img", 1L);
        Product productToSave = Product.toUpdatedProduct(request);

        // Product 저장 로직 구현
        save(productToSave);

        // 요청으로 보낼 dto
        String content = new ObjectMapper().writeValueAsString(request);

        // 해당 url로 post 요청
        MvcResult mvcResult = mvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andReturn();

        // response 객체 반환
        Map<String, Object> responseMap = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), Map.class);

        // 검증
        Assertions.assertEquals(responseMap.get("result"), true);
        Assertions.assertEquals(responseMap.get("status"), 201);
        Assertions.assertEquals(responseMap.get("message"), "상품이 등록되었습니다.");
        Assertions.assertEquals(responseMap.get("data"), "productId: 1");
    }

    private Product save(Product product) {
        product.assignId(++sequence);
        persistence.put(product.getId(), product);
        return product;
    }
}
