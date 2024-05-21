package com.godcare.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Getter
public class ViewProductListResponse {

    private final List<String> productPhotos;
    private final Long categoryId;
    private final Long productId;
    private final String productName;
    private final BigDecimal price;
    private final Integer quantity;
    private final Boolean anyOptions;
    private final String timeUpdated;

}
