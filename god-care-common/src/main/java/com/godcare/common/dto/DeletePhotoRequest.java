package com.godcare.common.dto;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Schema(description = "특정 이미지 파일 삭제 요청하는 DTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeletePhotoRequest {

    @NotBlank(message = "삭제할 이미지 URL을 입력해주세요.")
    @ApiModelProperty(value = "삭제할 이미지 파일 URL 입력 필드", dataType = "String")
    private String imgUrlToDelete;
}
