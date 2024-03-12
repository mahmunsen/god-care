package com.godcare.common.dto;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResisterProductRequest {

    private String mainImg;  // 메인 이미지(썸네일)
    private Long categoryId; // 카테고리 아이디

}
