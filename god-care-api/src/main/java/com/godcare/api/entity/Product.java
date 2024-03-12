package com.godcare.api.entity;

import com.godcare.common.dto.ResisterProductRequest;
import com.godcare.common.dto.UpdateProductRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Product {

    private String mainImg;
    private Long categoryId;
    private Long id;

    public void assignId(Long id) {
        this.id = id;
    }

    public static Product toUpdatedProduct(ResisterProductRequest request) {
        return Product.builder()
                .mainImg(request.getMainImg())
                .categoryId(request.getCategoryId())
                .build();
    }
    public static Product toUpdatedProduct(UpdateProductRequest updateProductRequest, Long id) {
        return Product.builder()
                .mainImg(updateProductRequest.getMainImg())
                .categoryId(updateProductRequest.getCategoryId())
                .id(id)
                .build();
    }
}
