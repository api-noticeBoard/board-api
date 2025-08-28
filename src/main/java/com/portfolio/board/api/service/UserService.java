package com.portfolio.board.api.service;

import com.portfolio.board.api.controller.AuthController;
import com.portfolio.board.api.domain.Member;
import com.portfolio.board.api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService {
    private final MemberRepository memberRepo;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입 비즈니스 로직을 수행합니다.
     * @Transactional 어노테이션을 통해 이 메서드 내의 모든 DB 작업은 하나의 트랜잭션으로 묶여 처리됩니다.
     * 중간에 오류가 발생하면 모든 작업이 롤백(rollback)되어 데이터 정합성을 보장합니다.
     *
     * @param request 회원가입 요청 정보
     * @return 생성된 Member 엔티티
     */
    @Transactional
    public Member signUp(AuthController.SignUpRequest request) {
        // 1. 사용자 이름(username) 중복 체크
        if (memberRepo.findByUsername(request.username()).isPresent()) {
            // 실무에서는 커스텀 예외를 정의하여 사용하는 것이 더 좋습니다.
            throw new IllegalArgumentException("이미 사용 중인 username 입니다.");
        }

        // 2. Member 엔티티 생성
        Member newMember = Member.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password())) // 비밀번호는 반드시 암호화
                .name(request.name())
                .roles(Collections.singletonList("ROLE_USER")) // 기본 권한 부여
                .build();

        // 3. 생성된 엔티티를 DB에 저장
        return memberRepo.save(newMember);
    }
}
