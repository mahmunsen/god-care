package com.godcare.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class ViewProductListResponse {

    private final String mainImg;
    private final Long categoryId;
    private final Long productId;
    private final String productName;
    private final BigDecimal price;
    private final Integer quantity;
    private final Boolean anyOptions;
    private final String timeUpdated;

}
