package com.godcare.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class ViewProductListResponse {

    private String mainImg;
    private Long categoryId;
    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private Boolean anyOptions;

}
