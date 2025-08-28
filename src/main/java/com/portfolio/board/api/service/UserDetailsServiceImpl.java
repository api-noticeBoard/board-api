package com.portfolio.board.api.service;

import com.portfolio.board.api.domain.Member;
import com.portfolio.board.api.repository.MemberRepository;
import com.portfolio.common.system.exception.BusinessException;
import com.portfolio.common.system.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Security의 UserDetailsService를 구현한 클래스.
 * 로그인 요청 시 사용자 정보를 DB에서 조회하는 역할을 담당합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 전용 트랜잭션으로 성능 최적화
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepo;

    /**
     * 사용자 이름(username)을 기반으로 DB에서 사용자 정보를 조회하여 UserDetails 객체로 반환합니다.
     * 이 메서드는 Spring Security가 로그인 처리 과정에서 내부적으로 호출합니다.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // MemberRepository를 사용하여 DB에서 사용자 정보를 조회합니다.
        Member member = memberRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // 조회된 Member 엔티티 정보를 바탕으로 Spring Security가 사용하는 UserDetails 객체를 생성하여 반환합니다.
        // password는 DB에 저장된 암호화된 비밀번호 그대로 전달해야 합니다.
        // authorities는 권한 목록을 전달합니다.
        return User.builder()
                .username(member.getUsername())
                .password(member.getPassword())
                .roles(member.getRoles().toArray(new String[0])) // roles 리스트를 배열로 변환
                .build();
    }
}
