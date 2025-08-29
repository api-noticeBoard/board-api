package com.portfolio.board.config;

import com.portfolio.board.config.filter.JwtAuthenticationFilter;
import com.portfolio.board.config.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // [1] 불필요한 인증 방식 비활성화
                // REST API 서버는 토큰 기반 인증을 사용하므로, HttpSession을 생성하는 formLogin과 httpBasic 인증 방식을 사용하지 않습니다.
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // [2] CSRF(Cross-Site Request Forgery) 보호 비활성화
                // 세션을 사용하지 않는 STATELESS 방식에서는 CSRF 공격에 비교적 안전하므로 비활성화합니다.
                .csrf(AbstractHttpConfigurer::disable)

                // [3] 세션 관리 정책을 STATELESS로 설정
                // 가장 중요한 설정 중 하나입니다. 서버가 클라이언트의 상태를 저장하지 않도록 하여,
                // 모든 요청이 토큰을 통해 독립적으로 인증되도록 강제합니다.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // [4] 익명 인증(Anonymous Authentication) 비활성화
                // JwtAuthenticationFilter가 인증 정보를 처리한 후, AnonymousAuthenticationFilter가
                // 이를 덮어쓰는 문제를 방지하기 위해 비활성화합니다.
                .anonymous(AbstractHttpConfigurer::disable)

                // [5] H2 콘솔 및 Swagger UI를 위한 프레임 옵션 허용 (개발 환경용)
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin()) // H2 콘솔은 iframe을 사용하므로 허용
                )

                // [6] HTTP 요청에 대한 인가(Authorization) 규칙 설정
                // 규칙은 구체적인 경로를 먼저, 넓은 범위를 나중에 선언해야 합니다.
                .authorizeHttpRequests(auth -> auth
                        // (6-1) 아래 경로들은 인증 여부와 상관없이 '누구나' 접근을 허용합니다. (permitAll)
                        .requestMatchers(
                                "/api/auth/**",      // 회원가입, 로그인 API
                                "/swagger-ui/**",    // Swagger UI 페이지
//                                "/v3/api-docs/**",   // Swagger API 문서
                                "/v1/api/**",         // application.yml에 맞게 수정
                                "/h2-console/**",    // H2 데이터베이스 콘솔
                                "/favicon.ico"
                        ).permitAll()

                        // (6-2) '/api/admin/**' 패턴의 경로는 'ADMIN' 역할을 가진 사용자만 접근을 허용합니다.
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/member/**").authenticated()

                        // (6-3) 위에서 정의한 경로 외의 '모든' 나머지 요청은 '반드시 인증'을 거쳐야만 접근을 허용합니다.
                        .anyRequest().authenticated()
                )
                // [7] 직접 구현한 JwtAuthenticationFilter를 Spring Security 필터 체인에 추가
                // UsernamePasswordAuthenticationFilter (Spring의 기본 로그인 처리 필터) 보다 먼저 실행되도록 설정하여,
                // JWT 토큰 검증이 로그인 처리보다 우선적으로 이루어지게 합니다.
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

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
