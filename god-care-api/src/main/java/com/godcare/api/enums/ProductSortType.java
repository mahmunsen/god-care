package com.godcare.api.enums;

import com.godcare.api.exception.ProductSortTypeNotFoundException;

public enum ProductSortType {
    SALES, // 판매순
    PRICE_ASC, // 가격 낮은순
    PRICE_DESC, // 가격 높은순
    CREATED_AT;  // 최신순

    public static ProductSortType from(String value) {
        try {
            if (value == null || "".equals(value)) value = "CREATED_AT";
            return ProductSortType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ProductSortTypeNotFoundException();
        }
    }
}
