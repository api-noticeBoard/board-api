package com.portfolio.board.api.dto;

import com.portfolio.board.api.domain.Category;
import com.portfolio.board.api.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class MemberResponse {
    /**
     * 사용자 정보 조회 시 응답으로 사용될 DTO.
     */
    @Getter
    public static class UserInfoResponse {
        private final Long id;
        private final String username;
        private final String name;
        private final List<String> roles;

        public UserInfoResponse(Member member) {
            this.id = member.getId();
            this.username = member.getUsername();
            this.name = member.getName();
            this.roles = member.getRoles();
        }
    }
}
