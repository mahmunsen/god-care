package com.godcare.common.dto;

import lombok.Getter;

@Getter
public class DeletePhotoResponse {
    private Long productPhotoId;
    private String deletedUrl;
    public DeletePhotoResponse(Long productPhotoId, String deletedUrl) {
        this.productPhotoId = productPhotoId;
        this.deletedUrl = deletedUrl;
    }
}
