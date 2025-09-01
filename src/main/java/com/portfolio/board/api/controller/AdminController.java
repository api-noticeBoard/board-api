package com.portfolio.board.api.controller;

import com.portfolio.board.api.dto.MemberRequest;
import com.portfolio.board.api.dto.MemberResponse;
import com.portfolio.board.api.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin", description = "관리자 관련 API")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;

    /**
     * 관리자가 특정 사용자의 정보를 수정하는 API.
     * @param userId 수정할 사용자의 ID (경로 변수)
     * @param request 수정할 사용자 정보 DTO
     * @return 성공 메시지
     */
    @PutMapping("/users/{userId}")
    @Operation(summary = "사용자 정보 수정", description = "관리자가 특정 사용자의 정보를 수정하는 API")
    public ResponseEntity<String> updateUserInfoByAdmin(
            @PathVariable Long userId,
            @Valid @RequestBody MemberRequest.UpdateUserInfoRequest request) {

        memberService.updateUserInfoByAdmin(userId, request);
        return ResponseEntity.ok("관리자에 의해 사용자(ID: " + userId + ") 정보가 성공적으로 수정되었습니다.");
    }

    /**
     * 관리자가 특정 사용자의 정보를 조회하는 API.
     * @param userId 조회할 사용자의 ID
     * @return 해당 사용자의 정보를 담은 DTO
     */
    @GetMapping("/users/{userId}")
    @Operation(summary = "사용자 정보 조회", description = "관리자가 특정 사용자의 정보를 조회하는 API")
    public ResponseEntity<MemberResponse.UserInfoResponse> getUserInfoByAdmin(@PathVariable Long userId) {
        MemberResponse.UserInfoResponse userInfo = memberService.getUserInfo(userId);
        return ResponseEntity.ok(userInfo);
    }
}
