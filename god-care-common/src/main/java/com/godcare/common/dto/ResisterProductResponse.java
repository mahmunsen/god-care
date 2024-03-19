package com.godcare.common.dto;

import lombok.*;

@ToString
@Builder
@Getter
@NoArgsConstructor
public class ResisterProductResponse {
    private Long productId;
    public ResisterProductResponse(Long productId) {
        this.productId = productId;
    }
}
