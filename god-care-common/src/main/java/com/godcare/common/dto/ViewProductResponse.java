package com.godcare.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ViewProductResponse {

    private Long productId;
    private String mainImg;
    private Long categoryId;

}
