package com.godcare.api.entity;

import com.godcare.common.dto.ResisterProductRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProductV0 {
    private Long id;
    private String mainImg;
    private Long categoryId;

    public void assignId(Long id) {
        this.id = id;
    }
    public static ProductV0 from(ResisterProductRequest request) {
        Long id = null;
        String mainImg = request.getMainImg();
        Long categoryId = request.getCategoryId();
        return new ProductV0(id, mainImg, categoryId);
    }

    public void update(String mainImg, Long categoryId) {
      this.mainImg = mainImg;
      this.categoryId = categoryId;
    }
}
