package com.godcare.api.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.godcare.api.advice.annotation.TimeTrace;
import com.godcare.api.config.BucketProperties;
import com.godcare.api.entity.FileStatus;
import com.godcare.api.enums.FilePath;
import com.godcare.api.exception.FileUploadFailedException;
import com.godcare.api.util.FileUtils;
import com.godcare.common.dto.FileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileService {

    private final S3AsyncClient s3AsyncClient;
    private final AmazonS3 amazonS3;
    private final BucketProperties bucketProperties;

    public List<FileResponse> getPresignedUrls(List<MultipartFile> multipartFiles) {
        List<FileResponse> fileResponseList = new ArrayList<>();
        for(MultipartFile multipartFile: multipartFiles) {
            String originalFileName = multipartFile.getOriginalFilename();
            String filePath = FilePath.PRODUCT_DIR.getPath();
            String fileName = getNewFileName(originalFileName);
            String keyName = filePath + "/" + fileName;
            String uploadFileUrl = "https://kr.object.ncloudstorage.com/" + bucketProperties.getBucketName() + "/" + keyName;
            GeneratePresignedUrlRequest generatePresignedUrlRequest = getGeneratePreSignedUrlRequest(bucketProperties.getBucketName(), keyName);
            URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
            url.toString();
            fileResponseList.add(new FileResponse(originalFileName, fileName, filePath, uploadFileUrl, url));
        }
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

    public String getNewFileName(String fileName) {
        String ext = fileName.substring(fileName.indexOf(".") + 1);
        return String.valueOf(Instant.now()) + UUID.randomUUID() + "." + ext;
    }


    // 이미지 파일 업데이트
    public FileResponse updateFile(MultipartFile multipartFile, String filePath, String imgToDelete) {

        amazonS3Client.deleteObject(bucketName + "/" + filePath, imgToDelete);

        String originalFileName = multipartFile.getOriginalFilename();
        String uploadFileName = getNewFileName(originalFileName);
        String uploadFileUrl = "";

        ObjectMetadata objectMetadata = getObjectMetadata(multipartFile);

        return getFileResponse(multipartFile, filePath, originalFileName, uploadFileName, uploadFileUrl, objectMetadata);
    }

    private static ObjectMetadata getObjectMetadata(MultipartFile multipartFile) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());
        return objectMetadata;
    }
}
