package com.godcare.api.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.*;
import com.godcare.api.config.BucketProperties;
import com.godcare.api.enums.FilePath;
import com.godcare.api.exception.FileAlreadyExistsException;
import com.godcare.common.dto.PresignedUrlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileService {

    private final AmazonS3 amazonS3;
    private final BucketProperties bucketProperties;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public CompletableFuture<List<PresignedUrlResponse>> getPresignedUrls(List<String> uploadFileNames) {

        String filePath = FilePath.PRODUCT_DIR.getPath();

        CompletableFuture<List<PresignedUrlResponse>> fileResponseList = CompletableFuture.supplyAsync(() ->
                uploadFileNames.parallelStream()
                        .map(uploadFileName -> filePath + "/" + uploadFileName)
                        .map(keyName -> {
                            // validation check
                            boolean alreadyNameExists = amazonS3.doesObjectExist(bucketProperties.getBucketName(), keyName);
                            if (alreadyNameExists) throw new FileAlreadyExistsException();
                            return keyName;
                        })
                        .map(keyName -> getGeneratePreSignedUrlRequest(bucketProperties.getBucketName(), keyName))
                        .map(generatePresignedUrlRequest -> amazonS3.generatePresignedUrl(generatePresignedUrlRequest))
                        .map(url -> new PresignedUrlResponse(url.toString()))
                        .collect(Collectors.toList()));

        return fileResponseList;
    }


    /**
     * 파일 업로드용(PUT) presigned url 생성
     *
     * @param bucket   버킷 이름
     * @param fileName S3 업로드용 파일 이름
     */
    private GeneratePresignedUrlRequest getGeneratePreSignedUrlRequest(String bucket, String fileName) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, fileName)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(getPreSignedUrlExpiration());
        generatePresignedUrlRequest.addRequestParameter(
                Headers.S3_CANNED_ACL,
                CannedAccessControlList.PublicRead.toString());
        return generatePresignedUrlRequest;
    }


    // presigned url 유효 기간 설정
    private Date getPreSignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 10;
        expiration.setTime(expTimeMillis);
        return expiration;
    }
}
