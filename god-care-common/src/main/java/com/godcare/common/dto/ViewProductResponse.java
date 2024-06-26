package com.godcare.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "특정 상품 조회 DTO")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ViewProductResponse {

    private Long productId;
    private List<String> imgUrls;
    private Long categoryId;
    private String categoryName;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private Boolean anyOptions;
}
