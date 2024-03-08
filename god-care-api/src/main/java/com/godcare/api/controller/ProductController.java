package com.godcare.api.controller;

import com.godcare.common.dto.ResisterProductRequest;
import com.godcare.api.util.ApiUtils;
import com.godcare.api.vo.Response;
import com.godcare.api.service.Product;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Api(tags = "상품 API")
public class ProductController<V> {
    private Map<Long, Product> persistence = new HashMap<>();
    private Long sequence = 0L;
    @Operation(summary = "상품 등록 API ", description = "새로운 상품을 업로드하는 API")
    @PostMapping(path="")
    public ResponseEntity<Response<Object>> uploadProduct(@RequestBody ResisterProductRequest resisterProductRequest){

        Product savedProduct = save(Product.toProduct(resisterProductRequest));

        return ApiUtils.success(HttpStatus.CREATED.value(), "상품이 등록되었습니다.", "productId: " + savedProduct.getId());
    }
    private Product save(Product product) {
        product.assignId(++sequence);
        persistence.put(product.getId(), product);
        return product;
    }


    /** todo */
    @Operation(summary = "상품 전체 조회 API ", description = "전체 상품 조회하는 API")
    @GetMapping(path="")
    public ResponseEntity<Response<Object>> viewProductList(){

        return ApiUtils.success(HttpStatus.OK.value(), "상품 리스트 전체 조회에 성공하였습니다.", null);
    }

    /** todo */
    @Operation(summary = "상품 수정하는 API ", description = "등록된 상품 정보 수정하는 API")
    @PatchMapping(path="/{product_id}")
    public ResponseEntity<Response<Object>> updateProduct(@PathVariable(value = "product_id") Long productId){

        productId = 2L;

        return ApiUtils.success(HttpStatus.OK.value(), "상품 정보가 정상적으로 업데이트되었습니다.", "productId: " + productId);
    }

    /** todo */
    @Operation(summary = "상품 삭제 API ", description = "등록된 상품 삭제하는 API")
    @DeleteMapping(path="/{product_id}")
    public ResponseEntity<Response<Object>> deleteProduct(@PathVariable(value = "product_id") Long productId){

        productId = 3L;

        return ApiUtils.success(HttpStatus.OK.value(), "상품이 정상적으로 삭제되었습니다.", "productId: " + productId);
    }
}
