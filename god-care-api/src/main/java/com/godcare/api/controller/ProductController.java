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
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Api(tags = "상품 API")
public class ProductController {

    private final ProductService productService;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "상품 등록 API ", description = "새로운 상품을 등록하는 API")
    @PostMapping(path = "")
    public CompletableFuture<Response<ResisterProductResponse>> addProduct() {

        return productService.addProduct().thenApply((product) -> Response.success(new ResisterProductResponse(product.getId())));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "이미지 등록 API ", description = "상품의 이미지를 등록하는 API")
    @PostMapping(path = "/{product_id}/product_photo")
    public CompletableFuture<Response<UploadPhotoResponse>> uploadImgUrls(@PathVariable(value = "product_id") Long productId, @RequestBody UploadPhotoRequest uploadPhotoRequest) {

        return productService.uploadImgUrls(uploadPhotoRequest, productId).thenApply((product) -> Response.success(new UploadPhotoResponse(product.getId())));
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "상품 조회 API ", description = "특정 상품 상세 조회 하는 API")
    @GetMapping(path = "/{product_id}")
    public CompletableFuture<Response<ViewProductResponse>> viewProduct(@PathVariable(value = "product_id") Long productId) {

        return productService.getProduct(productId).thenApplyAsync(viewProductResponse -> Response.success(viewProductResponse));
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "상품 전체 조회 API ", description = "전체 상품 조회하는 API")
    @GetMapping(path = "")
    public Response<PageResponse<ViewProductListResponse>> getProductListTwo(@ApiParam(value = "정렬 기준") @RequestParam(value = "order", required = false) String order, PageableRequest pageable) {
        PageResponse<ViewProductListResponse> productList = productService.getProductsList(order, pageable);
        return Response.success(productList);
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "단수 이미지 삭제 API ", description = "이미지 파일 하나씩 삭제 하는 API")
    @DeleteMapping(path = "/{product_id}/product_photo")
    public Response<DeletePhotoResponse> deletePhoto(@PathVariable(value = "product_id") Long productId, @RequestBody DeletePhotoRequest deletePhotoRequest) {

        DeletePhotoResponse deletePhotoResponse = productService.deletePhoto(productId, deletePhotoRequest.getImgUrlToDelete());

        return Response.success(deletePhotoResponse);
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "상품 정보 등록 완료 하는 API ", description = "등록된 상품 정보 업데이트 하는 API")
    @PatchMapping(path = "/{product_id}")
    public Response<UpdateProductResponse> updateProduct(@PathVariable(value = "product_id") Long productId, @RequestBody UpdateProductRequest updateProductRequest) {

        Product product = productService.updateProduct(productId, updateProductRequest);

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
