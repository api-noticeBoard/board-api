package com.portfolio.board.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Springdoc OpenAPI 관련 설정
 */
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI openAPI(){
        Info info = new Info()
                .title("게시판 프로젝트 API 명세서")
                .description("게시판 서비스 및 Core 개발을 위한 API 문서")
                .version("v1.0.0");

        return new OpenAPI()
                .components(new Components())
                // components 필요 시 추가
                .info(info);
    }


}
