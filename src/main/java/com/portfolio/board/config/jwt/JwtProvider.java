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

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
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

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("roles", String.class).split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet());

        AuthUser principal = new AuthUser(userId, username, (Set<String>) authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet()));

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
