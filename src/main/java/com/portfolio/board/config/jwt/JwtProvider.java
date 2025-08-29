package com.portfolio.board.config.jwt;

import com.portfolio.common.business.user.AuthUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret.key}")
    private String salt;

    private SecretKey secretKey;

    @Value("${jwt.token.expire.milliseconds}")
    private long expire;

    @PostConstruct
    protected void init(){
        secretKey = Keys.hmacShaKeyFor(salt.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(AuthUser authUser){
        Date now = new Date();
        Date validity = new Date(now.getTime() + expire);

        Claims claims = Jwts.claims()
                .subject(authUser.userId().toString())
                .issuedAt(now)
                .expiration(validity)
                .add("username", authUser.username())
                .add("roles", authUser.roles().stream().collect(Collectors.joining(",")))
                .build();

        return Jwts.builder()
                .claims(claims)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public Authentication getAuthentication(String token) {

        Claims claims = parseClaims(token);

        Long userId = Long.valueOf(claims.getSubject());
        String username = claims.get("username", String.class);

        // ✨ [핵심 수정] roles 클레임이 null이거나 비어있는 경우를 대비한 방어 코드 추가
        String rolesString = claims.get("roles", String.class);
        Collection<? extends GrantedAuthority> authorities;
        Set<String> rolesSet;

        if (StringUtils.hasText(rolesString)) {
            // roles 클레임이 존재할 경우
            String[] rolesArr = rolesString.split(",");

            authorities = Arrays.stream(rolesArr)
                    // ✨ 빈 문자열이 SimpleGrantedAuthority 생성자로 들어가는 것을 방지
                    .filter(StringUtils::hasText)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());

            rolesSet = Arrays.stream(rolesArr)
                    .filter(StringUtils::hasText)
                    .collect(Collectors.toSet());
        }else{
            // roles 클레임이 아예 없는 경우 (또는 비어있는 경우)
            authorities = Collections.emptySet();
            rolesSet = Collections.emptySet();
        }

        // ✨ [핵심 수정] UserDetails 타입의 객체를 principal로 사용해야 합니다.
        // 우리는 AuthUser가 UserDetails를 구현하도록 만들었습니다.
        AuthUser principal = new AuthUser(
                userId,
                username,
                "", // 비밀번호는 토큰에 없으므로 빈 문자열
                rolesSet); // roles 문자열 배열을 Set으로 변환하여 전달

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
