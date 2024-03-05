package com.godcare.core.service;

import com.godcare.common.dto.ResisterProductRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@Getter
public class Product {

    private String mainImg;
    private Long categoryId;
    private Long id;

    public void assignId(Long id) {
        this.id = id;
    }

    public static Product toProduct(ResisterProductRequest request) {
        return Product.builder()
                .mainImg(request.getMainImg())
                .categoryId(request.getCategoryId())
                .build();
    }
}
