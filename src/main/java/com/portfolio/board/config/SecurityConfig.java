package com.portfolio.board.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
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
                                "/api/auth/**"
                                , "/swagger/**"
                                , "/swagger-ui/**"
                                , "/v1/api/**").permitAll()
                        // 개발 편의를 위해 API 경로 전체를 임시로 허용하는 규칙 추가
                        .requestMatchers("/api/**").permitAll()
                        // 그 외 나머지 모든 요청은 반드시 인증(로그인)을 거쳐야 합니다.
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    /**
     * ✨ [해결 코드] PasswordEncoder를 빈으로 등록합니다.
     * 이 빈이 등록되어 있어야 Spring이 필요한 곳(AuthController, UserService 등)에
     * PasswordEncoder를 주입해줄 수 있습니다.
     *
     * PasswordEncoderFactories.createDelegatingPasswordEncoder()는
     * 다양한 암호화 알고리즘을 지원하며, 비밀번호 앞에 {bcrypt}, {noop} 등을 붙여
     * 어떤 알고리즘으로 암호화되었는지 명시할 수 있는 최신 방식의 PasswordEncoder입니다.
     * 기본적으로는 BCrypt를 사용합니다.
     * @return PasswordEncoder 구현체
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
