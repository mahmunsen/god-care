package com.godcare.api.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "aws", ignoreUnknownFields = false)
public class AsyncBucketProperties {

    private final String accessKey;
    private final String secretKey;
    private final String bucketName;
    private final String regionName;
    private final String endPoint;
    private final int multipartMinPartSize; // AWS S3 requires that file parts must have at least 5MB, except for the last part.

}
