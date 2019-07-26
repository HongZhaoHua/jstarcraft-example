package com.jstarcraft.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger配置器
 * 
 * @author Birdy
 *
 */
@Configuration
@EnableSwagger2
public class SwaggerConfigurer {

    @Bean
    public Docket getDocket() {
        ApiInfo api = new ApiInfoBuilder().build();
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(api);
    }

}