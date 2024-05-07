package com.godcare.api.controller;

import com.godcare.api.service.FileService;
import com.godcare.api.vo.Response;
import com.godcare.common.dto.FileResponse;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product_photos")
@RequiredArgsConstructor
@Api(tags = "상품 이미지 관련 API")
public class ProductPhotoController {

    private final FileService fileService;
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "presigned url 얻는 API ", description = "presigned url 얻는 API")
    @PostMapping(path = "/presigned_url")
    public Response<List<FileResponse>> getPresignedUrls(
            @RequestPart(value = "files") List<MultipartFile> multipartFiles) {
        return Response.success(fileService.getPresignedUrls(multipartFiles));
    }
}
