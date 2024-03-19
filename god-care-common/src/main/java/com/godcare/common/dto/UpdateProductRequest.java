package com.godcare.common.dto;

import lombok.*;

@EqualsAndHashCode
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {

    private String mainImg;
    private Long categoryId;

}
