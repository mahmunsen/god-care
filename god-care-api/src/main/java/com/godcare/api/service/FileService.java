package com.godcare.api.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.*;
import com.godcare.api.config.BucketProperties;
import com.godcare.api.enums.FilePath;
import com.godcare.api.exception.FileAlreadyExistsException;
import com.godcare.api.exception.FileUploadFailedException;
import com.godcare.common.dto.FileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileService {

    private final S3AsyncClient s3AsyncClient;
    private final AmazonS3 amazonS3;
    private final BucketProperties bucketProperties;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public CompletableFuture<List<FileResponse>> getPresignedUrls(List<String> uploadFileNames) {

        String filePath = FilePath.PRODUCT_DIR.getPath();

        List<CompletableFuture<FileResponse>> fileResponseList = uploadFileNames.stream().map(uploadFileName -> {
            String keyName = filePath + "/" + uploadFileName;

            // validation check
            boolean alreadyNameExists = amazonS3.doesObjectExist(bucketProperties.getBucketName(), keyName);
            if (alreadyNameExists) throw new FileAlreadyExistsException();
            return CompletableFuture.supplyAsync(() -> getGeneratePreSignedUrlRequest(bucketProperties.getBucketName(), keyName), threadPoolTaskExecutor)
                    .thenApplyAsync(generatePresignedUrlRequest -> amazonS3.generatePresignedUrl(generatePresignedUrlRequest), threadPoolTaskExecutor)
                    .thenApplyAsync(url -> new FileResponse(uploadFileName, url.toString()), threadPoolTaskExecutor);
        }).collect(Collectors.toList());

        return CompletableFuture.allOf(fileResponseList.toArray(new CompletableFuture[0])).thenApply(all -> fileResponseList.stream().map(CompletableFuture::join).collect(Collectors.toList()));
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
