package com.portfolio.board.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

public class PostRequest {

    @Getter @Setter
    public static class Create {
        @NotBlank(message = "제목은 비어 있을 수 없습니다.")
        @Size(max = 100, message = "제목은 100자를 넘을 수 없습니다.")
        private String title;

        @NotBlank(message = "내용은 비엉 있을 수 없습니다.")
        private String content;

        @NotNull(message = "카테고리ID는 필수입니다.")
        private Long categoryId;
    }
}
