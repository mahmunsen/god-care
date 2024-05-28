package com.godcare.common.dto;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@AllArgsConstructor
@Schema(description = "상품 정보 등록하는 DTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateProductRequest {

    @NotBlank(message = "카테고리 번호를 입력해주세요.")
    @ApiModelProperty(value = "카테고리 번호 입력 필드", dataType = "Long")
    private Long categoryId;

    @NotBlank(message = "상품 이름을 입력해주세요.")
    @ApiModelProperty(value = "상품 이름 입력 필드", dataType = "String")
    private String name;

    @NotBlank(message = "상품 가격을 입력해주세요.")
    @ApiModelProperty(value = "상품 가격 입력 필드", dataType = "BigDecimal")
    private BigDecimal price;

    @NotBlank(message = "상품 수량을 입력해주세요.")
    @ApiModelProperty(value = "상품 수량 입력 필드", dataType = "Integer")
    private Integer quantity;

    @NotBlank(message = "상품 옵션 여부를 입력해주세요.")
    @ApiModelProperty(value = "상품 옵션 여부 입력 필드", dataType = "Boolean")
    private Boolean anyOptions;

}
