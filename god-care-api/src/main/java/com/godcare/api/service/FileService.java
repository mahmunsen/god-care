package com.godcare.api.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.godcare.api.config.BucketProperties;
import com.godcare.api.enums.FilePath;
import com.godcare.common.dto.FileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileService {

    private final S3AsyncClient s3AsyncClient;
    private final AmazonS3 amazonS3;
    private final BucketProperties bucketProperties;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

//    public List<CompletableFuture<FileResponse>> getPresignedUrls(List<MultipartFile> multipartFiles) {
//        List<CompletableFuture<FileResponse>> fileResponseList = new ArrayList<>();
//        for (MultipartFile multipartFile : multipartFiles) {
//            String originalFileName = multipartFile.getOriginalFilename();
//            String fileName = getNewFileName(originalFileName);
//            String filePath = FilePath.PRODUCT_DIR.getPath();
//            String keyName = filePath + "/" + fileName;
//
//            CompletableFuture<FileResponse> fileResponse = CompletableFuture.supplyAsync(() ->
//                            getGeneratePreSignedUrlRequest(bucketProperties.getBucketName(), keyName))
//                    .thenApplyAsync(generatePresignedUrlRequest -> amazonS3.generatePresignedUrl(generatePresignedUrlRequest), threadPoolTaskExecutor)
//                    .thenApplyAsync(url -> {
//                        log.info(threadPoolTaskExecutor.toString());
//                        log.info("thread: %name", Thread.currentThread().getName());
//                        log.info("이 일을 처리하고 있는 녀석은?");
//                        return new FileResponse(originalFileName, fileName, url.toString());
//                    }, threadPoolTaskExecutor);
//            fileResponseList.add(fileResponse);
//        }
//        return fileResponseList;
//    }


//    public CompletableFuture<List<CompletableFuture<FileResponse>>> getPresignedUrls(List<MultipartFile> multipartFiles) {
//        CompletableFuture<List<CompletableFuture<FileResponse>>> fileResponseList = CompletableFuture.supplyAsync(() -> {
//            List<CompletableFuture<FileResponse>> fileRes = new ArrayList<>();
//            for (MultipartFile multipartFile : multipartFiles) {
//                String originalFileName = multipartFile.getOriginalFilename();
//                String fileName = getNewFileName(originalFileName);
//                String filePath = FilePath.PRODUCT_DIR.getPath();
//                String keyName = filePath + "/" + fileName;
//
//                CompletableFuture<FileResponse> fileResponse = CompletableFuture.supplyAsync(() ->
//                                getGeneratePreSignedUrlRequest(bucketProperties.getBucketName(), keyName))
//                        .thenApplyAsync(generatePresignedUrlRequest -> amazonS3.generatePresignedUrl(generatePresignedUrlRequest), threadPoolTaskExecutor)
//                        .thenApplyAsync(url -> {
//                            log.info(threadPoolTaskExecutor.toString());
//                            log.info("thread: %name", Thread.currentThread().getName());
//                            log.info("이 일을 처리하고 있는 녀석은?");
//                            return new FileResponse(originalFileName, fileName, url.toString());
//                        }, threadPoolTaskExecutor);
//                fileRes.add(fileResponse);
//            }
//            return fileRes;
//        });
//        return fileResponseList;
//    }


    public CompletableFuture<List<FileResponse>> getPresignedUrls(List<MultipartFile> multipartFiles) throws ExecutionException, InterruptedException {
        List<FileResponse> fileResponseList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            String originalFileName = multipartFile.getOriginalFilename();
            String fileName = getNewFileName(originalFileName);
            String filePath = FilePath.PRODUCT_DIR.getPath();
            String keyName = filePath + "/" + fileName;

            CompletableFuture<FileResponse> fileResponse = CompletableFuture.supplyAsync(() -> getGeneratePreSignedUrlRequest(bucketProperties.getBucketName(), keyName).join(), threadPoolTaskExecutor)
                    .thenApplyAsync(generatePresignedUrlRequest -> amazonS3.generatePresignedUrl(generatePresignedUrlRequest), threadPoolTaskExecutor)
                    .thenApplyAsync(url -> new FileResponse(originalFileName, fileName, url.toString()), threadPoolTaskExecutor);
            fileResponseList.add(fileResponse.get());
        }
        return CompletableFuture.completedFuture(fileResponseList);
    }

    /**
     * 파일 업로드용(PUT) presigned url 생성
     *
     * @param bucket   버킷 이름
     * @param fileName S3 업로드용 파일 이름
     */
    private CompletableFuture<GeneratePresignedUrlRequest> getGeneratePreSignedUrlRequest(String bucket, String fileName) {
        return CompletableFuture.supplyAsync(() -> new GeneratePresignedUrlRequest(bucket, fileName)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(getPreSignedUrlExpiration()), threadPoolTaskExecutor)
                .thenApplyAsync(generatePresignedUrlRequest -> {
                    generatePresignedUrlRequest.addRequestParameter(Headers.S3_CANNED_ACL, CannedAccessControlList.PublicRead.toString());
                    return generatePresignedUrlRequest;
                }, threadPoolTaskExecutor);
    }


    // presigned url 유효 기간 설정
    private Date getPreSignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 10;
        expiration.setTime(expTimeMillis);
        return expiration;
    }

    public String getNewFileName(String fileName) {
        String ext = fileName.substring(fileName.indexOf(".") + 1);
        return String.valueOf(Instant.now()) + UUID.randomUUID() + "." + ext;
    }

//
//    // 이미지 파일 업데이트
//    public FileResponse updateFile(MultipartFile multipartFile, String filePath, String imgToDelete) {
//
//        amazonS3Client.deleteObject(bucketName + "/" + filePath, imgToDelete);
//
//        String originalFileName = multipartFile.getOriginalFilename();
//        String uploadFileName = getNewFileName(originalFileName);
//        String uploadFileUrl = "";
//
//        ObjectMetadata objectMetadata = getObjectMetadata(multipartFile);
//
//        return getFileResponse(multipartFile, filePath, originalFileName, uploadFileName, uploadFileUrl, objectMetadata);
//    }

    private static ObjectMetadata getObjectMetadata(MultipartFile multipartFile) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());
        return objectMetadata;
    }
}
