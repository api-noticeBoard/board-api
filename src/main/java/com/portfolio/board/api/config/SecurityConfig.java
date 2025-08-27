package com.portfolio.board.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF(Cross-Site Request Forgery) 보호를 비활성화합니다.
                //    H2 콘솔은 CSRF 토큰을 사용하지 않으므로 비활성화해야 접근 가능합니다.
                //    실제 운영 환경에서는 API 성격에 맞게 선택적으로 적용해야 합니다.
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable())
                )
                // 2. HTTP 요청에 대한 접근 권한을 설정합니다.
                .authorizeHttpRequests(auth -> auth
                        // "/h2-console/**" 경로의 모든 요청은 인증 없이 허용(permitAll)합니다.
                        .requestMatchers(
                                "/h2-console/**"
                                , "/favicon.ico").permitAll()
                        // Swagger UI 관련 경로도 허용합니다.
                        // ▼▼▼ yml 파일에 설정된 경로(/v1/api)를 추가합니다. ▼▼▼
                        .requestMatchers(
                                "/swagger-ui/**"
                                , "/swagger/**"
                                , "/v1/api/**").permitAll()
                        // 그 외 나머지 모든 요청은 반드시 인증(로그인)을 거쳐야 합니다.
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
