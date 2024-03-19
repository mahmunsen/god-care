package com.godcare.api.controller;

import com.godcare.api.service.ProductService;
import com.godcare.common.dto.ResisterProductRequest;
import com.godcare.api.vo.Response;
import com.godcare.api.entity.Product;
import com.godcare.common.dto.ResisterProductResponse;
import com.godcare.common.dto.UpdateProductRequest;
import com.godcare.common.dto.ViewProductResponse;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.*;


@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Api(tags = "상품 API")
public class ProductController {

    private final ProductService productService;

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "상품 등록 API ", description = "새로운 상품을 업로드하는 API")
    @PostMapping(path = "")
    public Response<ResisterProductResponse> addProduct(@RequestBody ResisterProductRequest resisterProductRequest) {

        Product savedProduct = productService.addProduct(Product.toProduct(resisterProductRequest));

        return Response.success(new ResisterProductResponse(savedProduct.getId()));
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "상품 조회 API ", description = "특정 상품 상세조회하는 API")
    @GetMapping(path = "/{product_id}")
    public Response<ViewProductResponse> viewProduct(@PathVariable(value = "product_id") Long productId) {
        ViewProductResponse viewProductResponse = productService.getProduct(productId);

        return Response.success(viewProductResponse);
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "상품 전체 조회 API ", description = "전체 상품 조회하는 API")
    @GetMapping(path = "")
    public Response<List<ViewProductResponse>> viewProductList() {

        List<ViewProductResponse> productList = productService.getAllProducts();

        return Response.success(productList);
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "상품 수정하는 API ", description = "등록된 상품 정보 수정하는 API")
    @PatchMapping(path = "/{product_id}")
    public Response<String> updateProduct(@PathVariable(value = "product_id") Long productId, @RequestBody UpdateProductRequest updateProductRequest) {

        Product product = productService.updateProduct(productId, updateProductRequest);

        return Response.success("productId: " + product.getId());
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "상품 삭제 API ", description = "등록된 상품 삭제하는 API")
    @DeleteMapping(path = "/{product_id}")
    public Response<String> deleteProduct(@PathVariable(value = "product_id") Long productId) {

        productService.deleteProduct(productId);

        return Response.success("productId: " + productId);
    }
}
