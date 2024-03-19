package com.godcare.api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.godcare.api.entity.Product;
import com.godcare.api.repository.ProductRepository;
import com.godcare.api.service.ProductService;
import com.godcare.api.vo.Response;
import com.godcare.common.dto.ViewProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProductApiTest2 {
    @InjectMocks
    ProductController productController;
    @Mock
    ProductService productService;
    @Mock
    ProductRepository productRepository;  // 컨트롤러 테스트에서는 필요없을지도..?
    MockMvc mvc;

    @BeforeEach
    void beforeEach() {
        mvc = MockMvcBuilders.standaloneSetup(productController)  // 컨트롤러 설정
                .build();
    }

    @Test
    @DisplayName("상품을 단건 조회한다.")
    void viewProduct() throws Exception {
        // given
        Long id = 3L;

        // when, then
        Product product = Product.builder()
                .id(3L)
                .mainImg("ex.img")
                .categoryId(4L)
                .build();

        ViewProductResponse viewProductResponse = ViewProductResponse.builder()
                .productId(product.getId())
                .mainImg(product.getMainImg())
                .categoryId(product.getCategoryId())
                .build();

        when(productService.getProduct(anyLong())).thenReturn(viewProductResponse);
//        when(productRepository.findById(anyLong())).thenReturn(product);

        MvcResult mvcResult = mvc.perform(get("/api/v1/products/{product_id}", id))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        Response<ViewProductResponse> response = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<Response<ViewProductResponse>>() {
        });

        assertEquals(response.getSuccess(), true);
        assertEquals(response.getData().getProductId(),3L);
        assertEquals(response.getData().getMainImg(),"ex.img");
        assertEquals(response.getData().getCategoryId(), 4L);
        verify(productService).getProduct(id);
//        verify(productRepository).findById(id);  // 작동하지 않음
    }
}

