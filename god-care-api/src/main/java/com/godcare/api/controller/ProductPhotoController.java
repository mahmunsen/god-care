package com.godcare.api.controller;

import com.godcare.api.service.FileService;
import com.godcare.api.vo.Response;
import com.godcare.common.dto.PresignedUrlResponse;

import com.godcare.common.dto.PresignedUrlRequest;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/v1/product_photos")
@RequiredArgsConstructor
@Api(tags = "상품 이미지 관련 API")
public class ProductPhotoController {

    private final FileService fileService;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "presigned url 얻는 API ", description = "presigned url 얻는 API")
    @PostMapping(path = "/presigned_url")
    public CompletableFuture<Response<List<PresignedUrlResponse>>> getPresignedUrls(
            @RequestBody PresignedUrlRequest presignedUrlRequest) {
        return fileService.getPresignedUrls(presignedUrlRequest.getUploadFileNames()).thenApplyAsync((res) -> Response.success(res), threadPoolTaskExecutor);
    }
}
