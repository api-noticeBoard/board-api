package com.portfolio.board.api.service;

import com.portfolio.board.api.controller.AuthController;
import com.portfolio.board.api.domain.Member;
import com.portfolio.board.api.repository.MemberRepository;
import com.portfolio.board.config.jwt.JwtProvider;
import com.portfolio.common.business.user.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional(readOnly = true)
    public String signIn(AuthController.SignInRequest request) {
        // 1. 사용자 조회
        Member member = memberRepo.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        // 3. AuthUser 객체 생성
        AuthUser authUser = new AuthUser(
                member.getId(),
                member.getUsername(),
                member.getRoles().stream().collect(Collectors.toSet()).toString()
        );

        // 4. JWT 생성
        return jwtProvider.createToken(authUser);
    }
}
