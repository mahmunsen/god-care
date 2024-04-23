package com.godcare.api.service;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.godcare.api.advice.annotation.TimeTrace;
import com.godcare.api.exception.FileUploadFailedException;
import com.godcare.common.dto.FileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.*;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileService {

    private final S3AsyncClient s3AsyncClient;

    @Value("${spring.s3.bucket}")
    private String bucketName;

    public String getNewFileName(String fileName) {
        String ext = fileName.substring(fileName.indexOf(".") + 1);
        return String.valueOf(Instant.now()) + UUID.randomUUID() + "." + ext;
    }

    // 이미지 파일 업로드
    @TimeTrace
    public List<FileResponse> uploadFiles(List<MultipartFile> multipartFiles, String filePath) throws IOException {

        List<FileResponse> fileResponseList = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {

            String originalFileName = multipartFile.getOriginalFilename();
            String uploadFileName = getNewFileName(originalFileName);
            String uploadFileUrl = "";

            ObjectMetadata objectMetadata = getObjectMetadata(multipartFile);

            Map<String, String> metadata = new java.util.HashMap<>(Map.of("filename", multipartFile.getName()));

            String keyName = filePath + "/" + uploadFileName;

            // S3에 폴더 및 파일 업로드
            software.amazon.awssdk.services.s3.model.PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .metadata(metadata)
                    .acl(String.valueOf(CannedAccessControlList.PublicRead))
                    .key(keyName)
                    .contentLength(objectMetadata.getContentLength())
                    .contentType(objectMetadata.getContentType())
                    .build();

            // S3에 폴더 및 파일 업로드

            ByteBuffer byteBuffer = ByteBuffer.wrap(multipartFile.getBytes());

            CompletableFuture<PutObjectResponse> future = s3AsyncClient.putObject(putObjectRequest, AsyncRequestBody.fromByteBuffer(byteBuffer));

            future.whenComplete((resp, err) -> {
                try {
                    if (resp != null) {
                        System.out.println("Object uploaded. Details: " + resp);
                    } else {
                        err.printStackTrace();
                        throw new FileUploadFailedException();
                    }
                } finally {
                    s3AsyncClient.close();
                }
            });

            // S3에 업로드한 폴더 및 파일 URL
            uploadFileUrl = "https://kr.object.ncloudstorage.com/" + bucketName + "/" + keyName;

            fileResponseList.add(new FileResponse(originalFileName, uploadFileName, filePath, uploadFileUrl));
        }
        return fileResponseList;
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
