package com.godcare.api.controller;

import com.godcare.api.service.ProductService;
import com.godcare.common.dto.ResisterProductRequest;
import com.godcare.api.util.ApiUtils;
import com.godcare.api.vo.Response;
import com.godcare.api.entity.Product;
import com.godcare.common.dto.UpdateProductRequest;
import com.godcare.common.dto.ViewProductResponse;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Api(tags = "상품 API")
public class ProductController<V> {

    private final ProductService productService;

    @Operation(summary = "상품 등록 API ", description = "새로운 상품을 업로드하는 API")
    @PostMapping(path = "")
    public ResponseEntity<Response<Object>> addProduct(@RequestBody ResisterProductRequest resisterProductRequest) {

        Product savedProduct = productService.addProduct(Product.toProduct(resisterProductRequest));

        return ApiUtils.success(HttpStatus.CREATED.value(), "상품이 등록되었습니다.", "productId: " + savedProduct.getId());
    }

    @Operation(summary = "상품 조회 API ", description = "특정 상품 상세조회하는 API")
    @GetMapping(path = "/{product_id}")
    public ResponseEntity<Response<Object>> viewProduct(@PathVariable(value = "product_id") Long productId) {
        ViewProductResponse viewProductResponse = productService.getProduct(productId);

        return ApiUtils.success(HttpStatus.OK.value(), "상품 상세 페이지 조회에 성공하였습니다.", viewProductResponse);
    }

    @Operation(summary = "상품 전체 조회 API ", description = "전체 상품 조회하는 API")
    @GetMapping(path = "")
    public ResponseEntity<Response<Object>> viewProductList() {

        List<ViewProductResponse> productList = productService.getAllProducts();

        return ApiUtils.success(HttpStatus.OK.value(), "상품 리스트 전체 조회에 성공하였습니다.", productList);
    }

    @Operation(summary = "상품 수정하는 API ", description = "등록된 상품 정보 수정하는 API")
    @PatchMapping(path = "/{product_id}")
    public ResponseEntity<Response<Object>> updateProduct(@PathVariable(value = "product_id") Long productId, @RequestBody UpdateProductRequest updateProductRequest) {

        productService.updateProduct(productId, updateProductRequest);

        return ApiUtils.success(HttpStatus.OK.value(), "상품 정보가 정상적으로 업데이트되었습니다.", "productId: " + productId);
    }

    @Operation(summary = "상품 삭제 API ", description = "등록된 상품 삭제하는 API")
    @DeleteMapping(path = "/{product_id}")
    public ResponseEntity<Response<Object>> deleteProduct(@PathVariable(value = "product_id") Long productId) {

        productService.deleteProduct(productId);

        return ApiUtils.success(HttpStatus.OK.value(), "상품이 정상적으로 삭제되었습니다.", "productId: " + productId);
    }
}
