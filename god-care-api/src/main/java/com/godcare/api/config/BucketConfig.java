package com.godcare.api.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BucketConfig {

    private final BucketProperties bucketProperties;

    @Bean
    public BasicAWSCredentials BasicAWSCredentials(){
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(bucketProperties.getAccessKey(), bucketProperties.getSecretKey());
        return basicAWSCredentials;
    }

    @Bean
    public AmazonS3 amazonS3() {
        AmazonS3 s3Builder = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(bucketProperties.getEndPoint(),
                        bucketProperties.getRegionName()))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(bucketProperties.getAccessKey(), bucketProperties.getSecretKey())))
                .build();
        return s3Builder;
    }
}
