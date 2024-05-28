package com.godcare.common.dto;

import lombok.Getter;

@Getter
public class DeletePhotoResponse {
    private Long productId;
    private String deletedUrl;
    public DeletePhotoResponse(Long productId, String deletedUrl) {
        this.productId = productId;
        this.deletedUrl = deletedUrl;
    }
}
