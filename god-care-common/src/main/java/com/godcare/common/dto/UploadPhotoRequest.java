package com.godcare.common.dto;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@AllArgsConstructor
@Schema(description = "이미지 업로드 요청하는 DTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UploadPhotoRequest {

    @NotBlank(message = "업로드 할 이미지 파일명 들을 입력해주세요.")
    @ApiModelProperty(value = "복수의 업로드 파일명 입력 필드", dataType = "String")
    List<String> productPhotoNames;
}
