package com.app2.engine.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket apiDocket() {
//        http://localhost:8083/swagger-ui.html
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
//                .apis(RequestHandlerSelectors.any()) //all
                .apis(RequestHandlerSelectors.basePackage("com.app2.engine.controller"))
                .paths(regex("/api.*"))
//                .paths(PathSelectors.any())// all
                .build();
    }
}