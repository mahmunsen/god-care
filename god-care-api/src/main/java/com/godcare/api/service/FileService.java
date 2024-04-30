package com.godcare.api.service;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.godcare.api.advice.annotation.TimeTrace;
import com.godcare.api.config.AsyncBucketProperties;
import com.godcare.api.entity.FileStatus;
import com.godcare.api.exception.FileUploadFailedException;
import com.godcare.api.util.FileUtils;
import com.godcare.common.dto.FileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileService {

    private final S3AsyncClient s3AsyncClient;
    private final AsyncBucketProperties asyncBucketProperties;

    public String getNewFileName(String fileName) {
        String ext = fileName.substring(fileName.indexOf(".") + 1);
        return String.valueOf(Instant.now()) + UUID.randomUUID() + "." + ext;
    }

    // 이미지 파일 업로드
    @TimeTrace
    public List<FileResponse> uploadFiles(List<MultipartFile> multipartFiles, String filePath) throws IOException, ExecutionException, InterruptedException {

        List<FileResponse> fileResponseList = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {

            String originalFileName = multipartFile.getOriginalFilename();
            String uploadFileName = getNewFileName(originalFileName);

//            String uploadFileUrl = "";
//            ObjectMetadata objectMetadata = getObjectMetadata(multipartFile);

            Map<String, String> metadata = new java.util.HashMap<>(Map.of("filename", multipartFile.getName()));

            String keyName = filePath + "/" + uploadFileName;
            // S3에 업로드한 폴더 및 파일 URL

            String uploadFileUrl = "https://kr.object.ncloudstorage.com/" + asyncBucketProperties.getBucketName() + "/" + keyName;

            // byteBuffer
            ByteBuffer byteBuffer = ByteBuffer.wrap(multipartFile.getBytes());

            CompletableFuture<CreateMultipartUploadResponse> s3AsyncClientMultipartUpload = s3AsyncClient.createMultipartUpload(CreateMultipartUploadRequest.builder()
                    .contentType(multipartFile.getContentType())
                    .key(keyName)
                    .metadata(metadata)
                    .bucket(asyncBucketProperties.getBucketName())
                    .acl(String.valueOf(CannedAccessControlList.PublicRead))
                    .build());

            FileStatus fileStatus = new FileStatus(Objects.requireNonNull(multipartFile.getContentType()), uploadFileName);

            CompletableFuture<FileResponse> fileResponse = s3AsyncClientMultipartUpload.whenComplete((resp, err) -> {
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
                    })
                    .thenApplyAsync(response -> {
                        checkResult(response);
                        fileStatus.setUploadId(response.uploadId());
                        log.info("Upload object with ID={}", response.uploadId());
                        return byteBuffer;
                    })
                    .thenApplyAsync(byteBfr -> {
                        // 바이트 수 축적
                        fileStatus.addBuffered(byteBfr.capacity() - byteBfr.remaining());
                        // 일정 크기가 되면 업로드 상태 객체의 누적 바이트 수를 0으로 초기화 후 return true
                        if (fileStatus.getBuffered() >= 5242880) {
                            fileStatus.setBuffered(0);
                        }
                        return FileUtils.getByteBuffer(byteBuffer);
                    })
                    .thenApplyAsync(byteBuffer1 -> {
                        uploadPart(fileStatus, byteBuffer1);
                        return fileStatus;
                    })
                    .thenApplyAsync(status -> completeMultipartUpload(status))
                    .thenApply((response2) -> {
                        return new FileResponse(originalFileName, uploadFileName, filePath, uploadFileUrl, fileStatus.getUploadId());
                    });

            fileResponse.thenApply((res) -> {
                return fileResponseList.add(res);
            });
        }
        return fileResponseList;
    }

    private CompletableFuture<CompleteMultipartUploadResponse> completeMultipartUpload(FileStatus status) {
        log.info("CompleteUpload - fileKey={}, completedParts.size={}",
                status.getFileKey(), status.getCompletedParts().size());

        CompletedMultipartUpload multipartUpload = CompletedMultipartUpload.builder()
                .parts(status.getCompletedParts().values())
                .build();

        return s3AsyncClient.completeMultipartUpload(CompleteMultipartUploadRequest.builder()
                .bucket(asyncBucketProperties.getBucketName())
                .uploadId(status.getUploadId())
                .multipartUpload(multipartUpload)
                .key(status.getFileKey())
                .build());
    }

    private CompletableFuture<CompletedPart> uploadPart(FileStatus fileStatus, ByteBuffer partData) {
        final int partNumber = fileStatus.getAddedPartCounter();

        // 파일 부분 업로드하기
        CompletableFuture<UploadPartResponse> uploadPartResponse = s3AsyncClient.uploadPart(UploadPartRequest.builder()
                        .bucket(asyncBucketProperties.getBucketName())
                        .key(fileStatus.getFileKey())
                        .uploadId(fileStatus.getUploadId())
                        .partNumber(partNumber)
                        .contentLength((long) partData.capacity())
                        .build(),
                AsyncRequestBody.fromByteBuffer(partData));

        return uploadPartResponse.thenApplyAsync(res -> {
            checkResult(res);
            return CompletedPart.builder()
                    .eTag(res.eTag())
                    .partNumber(partNumber)
                    .build();
        });
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

    private static void checkResult(SdkResponse result) {
        if (result.sdkHttpResponse() == null || !result.sdkHttpResponse().isSuccessful()) {
            throw new FileUploadFailedException();
        }
    }
}
