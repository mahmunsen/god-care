package com.godcare.api.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "spring.s3")
public class AsyncBucketProperties {

    private final String endPoint = "https://kr.object.ncloudstorage.com";
    private final String regionName = "kr-standard";
    private final String accessKey;
    private final String secretKey;
    private final String bucketName;
}
