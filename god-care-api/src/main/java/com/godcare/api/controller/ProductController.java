package com.godcare.api.controller;

import com.godcare.api.entity.Product;
import com.godcare.api.service.ProductService;
import com.godcare.api.vo.*;
import com.godcare.common.dto.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Api(tags = "상품 API")
public class ProductController {

    private final ProductService productService;

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "상품 등록 API ", description = "새로운 상품을 업로드하는 API")
    @PostMapping(path = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<ResisterProductResponse> addProduct(@ModelAttribute ResisterProductRequest resisterProductRequest, @ApiParam(value = "메인 이미지 파일") @RequestPart(value = "mainImg", required = true) MultipartFile mainImg) {

        Product savedProduct = productService.addProduct(resisterProductRequest, mainImg);

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
    @GetMapping(path = "/cursorTest")
    public Response<PageResponse<ViewProductListResponse>> getProductListTwo(@ApiParam(value = "정렬 기준") @RequestParam(value = "order", required = false) String order, PageableRequest pageable) {
        PageResponse<ViewProductListResponse> productList = productService.getProductsList(order, pageable);
        return Response.success(productList);
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "상품 수정하는 API ", description = "등록된 상품 정보 수정하는 API")
    @PatchMapping(path = "/{product_id}")
    public Response<UpdateProductResponse> updateProduct(@PathVariable(value = "product_id") Long productId, @ModelAttribute UpdateProductRequest updateProductRequest, @ApiParam(value = "메인 이미지 파일") @RequestPart(value = "mainImg", required = false) MultipartFile multipartFile) {

        Product product = productService.updateProduct(productId, updateProductRequest, multipartFile);

        return Response.success(new UpdateProductResponse(product.getId()));
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "상품 삭제 API ", description = "등록된 상품 삭제하는 API")
    @DeleteMapping(path = "/{product_id}")
    public Response<DeleteProductResponse> deleteProduct(@PathVariable(value = "product_id") Long productId) {

        productService.deleteProduct(productId);

        return Response.success(new DeleteProductResponse(productId));
    }
}
