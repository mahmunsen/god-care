package com.godcare.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@AllArgsConstructor
@Schema(description = "상품 수정 DTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateProductRequest {

    private Long categoryId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private Boolean anyOptions;

}
