package com.godcare.api;

import com.godcare.api.config.BucketProperties;
import com.godcare.api.config.JasyptConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({JasyptConfig.class, BucketProperties.class})
@SpringBootApplication(scanBasePackages = {"com.godcare.api", "com.godcare.common"})
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

}
