package com.portfolio.board.api.controller;

import com.portfolio.board.api.dto.MemberRequest;
import com.portfolio.board.api.dto.MemberResponse;
import com.portfolio.board.api.service.MemberService;
import com.portfolio.common.business.user.UserInfoHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member", description = "사용자 관련 API")
@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 현재 로그인한 사용자의 정보를 조회하는 API (내 정보 조회)
     * @return 현재 사용자의 정보를 담은 DTO
     */
    @GetMapping("/me")
    @Operation(summary = "로그인 사용자 조회", description = "현재 로그인한 사용자의 정보를 조회하는 API")
    public ResponseEntity<MemberResponse.UserInfoResponse> getMyInfo() {
        Long currentUserId = UserInfoHolder.getUserId();
        MemberResponse.UserInfoResponse userInfo = memberService.getUserInfo(currentUserId);
        return ResponseEntity.ok(userInfo);
    }

    /**
     * 현재 로그인한 사용자의 정보를 수정하는 API (내 정보 수정)
     * @param request 수정할 사용자 정보(이름 등)를 담은 DTO
     * @return 성공 메시지
     */
    @PutMapping("/me")
    @Operation(summary = "로그인 사용자 정보 수정", description = "현재 로그인한 사용자의 정보를 수정하는 API")
    public ResponseEntity<String> updateMyInfo(@Valid @RequestBody MemberRequest.UpdateUserInfoRequest request) {
        Long currentUserId = UserInfoHolder.getUserId();
        memberService.updateUserInfo(currentUserId, request);
        return ResponseEntity.ok("사용자 정보가 성공적으로 수정되었습니다.");
    }

    /**
     * 현재 로그인한 사용자가 비밀번호를 변경하는 API
     * @param request 현재 비밀번호와 새 비밀번호를 담은 DTO
     * @return 성공 메시지
     */
    @PutMapping("/me/password")
    @Operation(summary = "로그인 사용자 비밀번호 변경", description = "현재 로그인한 사용자가 비밀번호를 변경하는 API")
    public ResponseEntity<String> changeMyPassword(@Valid @RequestBody MemberRequest.ChangePasswordRequest request) {
        Long currentUserId = UserInfoHolder.getUserId();
        memberService.changeUserPassword(currentUserId, request);
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

    /**
     * 현재 로그인한 사용자가 탈퇴하는 API (소프트 삭제)
     * @return 성공 메시지
     */
    @DeleteMapping("/me")
    @Operation(summary = "로그인 사용자 탈퇴", description = "현재 로그인한 사용자가 탈퇴하는 API (소프트 삭제)")
    public ResponseEntity<String> withdraw() {
        Long currentUserId = UserInfoHolder.getUserId();
        memberService.withdrawUser(currentUserId);
        return ResponseEntity.ok("회원 탈퇴가 성공적으로 처리되었습니다.");
    }
}
