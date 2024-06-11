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
@Schema(description = "presigned URL 요청하는 DTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PresignedUrlRequest {

    @NotBlank(message = "업로드할 이미지 파일 이름들을 입력해주세요.")
    @ApiModelProperty(value = "복수 이미지 파일 이름 입력 필드", dataType = "String")
    private List<String> uploadFileNames;

}
