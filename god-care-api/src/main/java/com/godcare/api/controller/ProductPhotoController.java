package com.godcare.api.controller;

import com.godcare.api.service.FileService;
import com.godcare.api.vo.Response;
import com.godcare.common.dto.FileResponse;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping("/api/v1/product_photos")
@RequiredArgsConstructor
@Api(tags = "상품 이미지 관련 API")
public class ProductPhotoController {

    private final FileService fileService;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

//    @ResponseStatus(HttpStatus.CREATED)
//    @Operation(summary = "presigned url 얻는 API ", description = "presigned url 얻는 API")
//    @PostMapping(path = "/presigned_url")
//    public CompletableFuture<Response<List<CompletableFuture<FileResponse>>>> getPresignedUrls(
//            @RequestPart(value = "files") List<MultipartFile> multipartFiles) {
//        return fileService.getPresignedUrls(multipartFiles).thenApplyAsync((res) -> Response.success(res));
//    }
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "presigned url 얻는 API ", description = "presigned url 얻는 API")
    @PostMapping(path = "/presigned_url")
    public CompletableFuture<Response<List<FileResponse>>> getPresignedUrls(
            @RequestPart(value = "files") List<MultipartFile> multipartFiles) throws ExecutionException, InterruptedException {
        return fileService.getPresignedUrls(multipartFiles).thenApplyAsync((res) -> Response.success(res), threadPoolTaskExecutor);
    }
}
