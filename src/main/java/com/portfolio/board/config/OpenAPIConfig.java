package com.portfolio.board.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Springdoc OpenAPI 관련 설정
 */
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI openAPI(){
        // 1. API 문서의 기본 정보 설정
        Info info = new Info()
                .title("게시판 프로젝트 API 명세서")
                .description("게시판 서비스 및 Core 개발을 위한 API 문서")
                .version("v1.0.0");

        // ✨ --- JWT 인증 설정 추가 시작 --- ✨

        // 2. SecurityScheme 이름 정의
        String jwtSchemeName = "jwtAuth";

        // 3. API 요청 헤더에 인증 정보를 담을 방식을 정의합니다. (SecurityScheme)
        SecurityScheme securityScheme = new SecurityScheme()
                .name(jwtSchemeName) // Security Scheme의 이름을 지정합니다.
                .type(SecurityScheme.Type.HTTP) // 인증 타입은 HTTP
                .scheme("bearer") // Bearer 토큰 방식을 사용
                .bearerFormat("JWT"); // 토큰의 형식은 JWT

        // 4. API 문서의 Components에 위에서 정의한 SecurityScheme을 추가합니다.
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, securityScheme);

        // 5. 전역적으로 적용할 보안 요구사항(SecurityRequirement)을 정의합니다.
        //    모든 API에 대해 위에서 정의한 'jwtAuth' 스킴을 사용하도록 설정합니다.
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        // ✨ --- JWT 인증 설정 추가 끝 --- ✨

        // 6. 기존 OpenAPI 객체에 components와 security 설정을 추가하여 반환합니다.
        return new OpenAPI()
                .info(info)
                .components(components) // SecurityScheme을 포함한 components 설정
                .addSecurityItem(securityRequirement); // 전역 SecurityRequirement 설정
    }


}
