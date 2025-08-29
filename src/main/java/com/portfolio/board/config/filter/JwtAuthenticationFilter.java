package com.portfolio.board.config.filter;

import com.portfolio.board.config.jwt.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 클라이언트의 모든 HTTP 요청을 가로채서 JWT(JSON Web Token)의 유효성을 검증하는 필터입니다.
 * 이 필터는 Spring Security의 필터 체인에 등록되어, 컨트롤러에 요청이 도달하기 전에 먼저 실행됩니다.
 *
 * OncePerRequestFilter를 상속받아, 하나의 요청에 대해 단 한 번만 필터가 실행되도록 보장합니다.
 *
 * @RequiredArgsConstructor: final 필드를 위한 생성자를 Lombok이 자동으로 생성합니다.
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * JWT의 생성 및 검증 로직을 담당하는 JwtProvider를 의존성 주입 받습니다.
     */
    private final JwtProvider jwtProvider;

    /**
     * 실제 필터링 로직이 수행되는 메서드입니다.
     *
     * @param request      클라이언트의 HttpServletRequest
     * @param response     서버의 HttpServletResponse
     * @param filterChain  다음 필터를 호출하기 위한 필터 체인
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 1. 요청(Request) 헤더에서 JWT 토큰을 추출합니다.
        String token = resolveToken(request);

        // 2. 토큰이 존재하고(StringUtils.hasText) 유효한지(jwtProvider.validateToken) 검증합니다.
        if (StringUtils.hasText(token) && jwtProvider.validateToken(token)) {

            // 3. 토큰이 유효하면, 토큰으로부터 Authentication(인증 정보) 객체를 받아옵니다.
            //    이 Authentication 객체의 Principal에는 우리가 만든 AuthUser 객체가 들어있습니다.
            Authentication authentication = jwtProvider.getAuthentication(token);

            // 4. 받아온 인증 정보를 SecurityContextHolder에 저장합니다.
            //    SecurityContextHolder는 현재 요청을 처리하는 스레드 동안 인증 정보를 유지합니다.
            //    이 작업이 성공적으로 수행되면, Spring Security는 이 요청을 '인증된 사용자'의 요청으로 간주합니다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 5. 다음 필터로 요청과 응답을 전달합니다.
        //    만약 유효한 토큰이 없더라도, 다음 필터로 넘겨서 뒤에 있는 필터들이
        //    (예: URL 경로에 따라 접근을 허용하거나 차단하는 AuthorizationFilter) 처리하도록 합니다.
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청 헤더의 "Authorization" 필드에서 'Bearer ' 접두사를 제거하고 순수한 토큰 문자열만 추출하는 헬퍼 메서드입니다.
     * @param request HttpServletRequest
     * @return 추출된 토큰 문자열. 토큰이 없거나 형식이 잘못되었으면 null을 반환합니다.
     */
    private String resolveToken(HttpServletRequest request) {
        // "Authorization" 헤더 값을 가져옵니다.
        String bearerToken = request.getHeader("Authorization");

        // 헤더 값이 존재하고 "Bearer "로 시작하는지 확인합니다.
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // "Bearer " (7글자) 이후의 문자열을 잘라서 반환합니다.
            return bearerToken.substring(7);
        }
        return null;
    }
}
