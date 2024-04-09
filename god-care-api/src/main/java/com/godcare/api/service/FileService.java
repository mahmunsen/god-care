package com.godcare.api.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.godcare.common.dto.FileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileService {

    private final AmazonS3Client amazonS3Client;

    @Value("${spring.s3.bucket}")
    private String bucketName;

    public String getNewFileName(String fileName) {
        String ext = fileName.substring(fileName.indexOf(".") + 1);
        return String.valueOf(Instant.now()) + UUID.randomUUID() + "." + ext;
    }

    // 이미지 파일 업로드
    public FileResponse uploadFile(MultipartFile multipartFile, String filePath) {

        String originalFileName = multipartFile.getOriginalFilename();
        String uploadFileName = getNewFileName(originalFileName);
        String uploadFileUrl = "";

        ObjectMetadata objectMetadata = getObjectMetadata(multipartFile);

        return getFileResponse(multipartFile, filePath, originalFileName, uploadFileName, uploadFileUrl, objectMetadata);
    }

    private FileResponse getFileResponse(MultipartFile multipartFile, String filePath, String originalFileName, String uploadFileName, String uploadFileUrl, ObjectMetadata objectMetadata) {
        try (InputStream inputStream = multipartFile.getInputStream()) {

            String keyName = filePath + "/" + uploadFileName;

            // S3에 폴더 및 파일 업로드
            // withCannedAcl 로 publicRead로 바꿔주면 url로 접속시 바로 사진이 전체공개된다.
            amazonS3Client.putObject(
                    new PutObjectRequest(bucketName, keyName, inputStream, objectMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead));

            // S3에 업로드한 폴더 및 파일 URL
            uploadFileUrl = "https://kr.object.ncloudstorage.com/" + bucketName + "/" + keyName;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new FileResponse(originalFileName, uploadFileName, filePath, uploadFileUrl);
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
