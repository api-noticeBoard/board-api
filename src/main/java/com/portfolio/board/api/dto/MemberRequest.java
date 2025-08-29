package com.portfolio.board.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

public class MemberRequest {

    /**
     * 사용자 정보 수정 시 요청으로 사용될 DTO.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class UpdateUserInfoRequest {
        @NotBlank(message = "이름은 비어 있을 수 없습니다.")
        private String name;
    }

    /**
     * 비밀번호 변경 시 요청으로 사용될 DTO.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ChangePasswordRequest {
        @NotBlank(message = "현재 비밀번호는 필수입니다.")
        private String currentPassword;
        @NotBlank(message = "새 비밀번호는 필수입니다.")
        private String newPassword;
    }

}
