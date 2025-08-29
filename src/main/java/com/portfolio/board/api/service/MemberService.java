package com.portfolio.board.api.service;

import com.portfolio.board.api.controller.AuthController;
import com.portfolio.board.api.domain.Category;
import com.portfolio.board.api.domain.Member;
import com.portfolio.board.api.dto.CategoryRequest;
import com.portfolio.board.api.dto.CategoryResponse;
import com.portfolio.board.api.dto.MemberRequest;
import com.portfolio.board.api.dto.MemberResponse;
import com.portfolio.board.api.mapper.CategoryMapper;
import com.portfolio.board.api.repository.CategoryRepository;
import com.portfolio.board.api.repository.MemberRepository;
import com.portfolio.common.system.exception.BusinessException;
import com.portfolio.common.system.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입 비즈니스 로직
     */
    @Transactional
    public Member signUp(AuthController.SignUpRequest request) {
        if (memberRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 username 입니다.");
        }
        Member newMember = Member.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .roles(Collections.singletonList("ROLE_USER"))
                .build();
        return memberRepository.save(newMember);
    }

    /**
     * 사용자 ID로 정보를 조회하여 DTO로 반환하는 메서드. (공용)
     */
    @Transactional(readOnly = true)
    public MemberResponse.UserInfoResponse getUserInfo(Long userId) {
        Member member = findMemberById(userId);
        return new MemberResponse.UserInfoResponse(member);
    }

    /**
     * 일반 사용자가 자신의 정보를 수정하는 메서드
     */
    @Transactional
    public void updateUserInfo(Long userId, MemberRequest.UpdateUserInfoRequest request) {
        Member member = findMemberById(userId);
        member.changeName(request.getName());
    }

    /**
     * 일반 사용자가 자신의 비밀번호를 변경하는 메서드
     */
    @Transactional
    public void changeUserPassword(Long userId, MemberRequest.ChangePasswordRequest request) {
        Member member = findMemberById(userId);
        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        member.changePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    /**
     * 일반 사용자가 탈퇴하는 메서드 (소프트 삭제를 가정)
     */
    @Transactional
    public void withdrawUser(Long userId) {
        Member member = findMemberById(userId);
        // 실제로는 상태 값을 '탈퇴'로 변경하는 로직이 필요. (member.withdraw())
        // 여기서는 설명을 위해 DB에서 완전히 삭제.
        memberRepository.delete(member);
    }

    // --- 관리자용 메서드 ---

    /**
     * 관리자가 전체 사용자 목록을 조회하는 메서드
     */
    @Transactional(readOnly = true)
    public List<MemberResponse.UserInfoResponse> getAllUsers() {
        return memberRepository.findAll().stream()
                .map(MemberResponse.UserInfoResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 관리자가 특정 사용자의 정보를 수정하는 메서드
     */
    @Transactional
    public void updateUserInfoByAdmin(Long userId, MemberRequest.UpdateUserInfoRequest request) {
        Member member = findMemberById(userId);
        member.changeName(request.getName());
    }

    // TODO: flag로 삭제 메서드 만들기 (휴면계정, 1달 후 삭제)
    /**
     * 관리자가 특정 사용자를 삭제하는 메서드 (물리 삭제)
     */
    @Transactional
    public void deleteUserByAdmin(Long userId) {
        Member member = findMemberById(userId);
        memberRepository.delete(member);
    }

    // --- Private Helper Method ---

    /**
     * ID로 Member를 찾는 중복 로직을 추출한 private 메서드
     */
    private Member findMemberById(Long userId) {
        return memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("ID " + userId + "에 해당하는 사용자를 찾을 수 없습니다."));
    }
}
