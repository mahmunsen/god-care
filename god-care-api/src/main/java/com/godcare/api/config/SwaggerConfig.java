package com.godcare.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.godcare.api.controller")) //  Swagger API 문서로 만들기 원하는 BasePackage 경로. (필수)
                .paths(PathSelectors.any()) // URL 경로를 지정하여 해당 URL에 해당하는 요청만 Swagger API 문서로 만든다. (any()로 설정 시 모든 api가 보여짐, (필수))
                .build()
                .apiInfo(apiInfo()); // Swagger API 문서에 대한 설명을 표기하는 메소드. (선택)
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("God Care")
                .description("God-Care API 리스트입니다.")
                .version("1.0")
                .build();
    }


}
