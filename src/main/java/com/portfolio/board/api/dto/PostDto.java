package com.portfolio.board.api.dto;

import com.portfolio.board.api.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

public class PostDto {

    // --- 요청(Request) DTOs ---
    @Getter @Setter
    public static class CreateRequest {
        @NotBlank(message = "제목은 비어 있을 수 없습니다.")
        @Size(max = 100, message = "제목은 100자를 넘을 수 없습니다.")
        private String title;
        @NotBlank(message = "내용은 비어 있을 수 없습니다.") private String content;
        @NotNull(message = "카테고리ID는 필수입니다.") private Long categoryId;

        // DTO > Entity
//        public Post toEntity(Category category){
//            return Post.builder()
//                    .title(this.title)
//                    .content(this.content)
//                    .category(category)
//                    .build();
//        }
    }

    @Getter @Setter
    public static class UpdateRequest {
        @NotBlank(message = "제목은 비어 있을 수 없습니다.")
        @Size(max = 100, message = "제목은 100자를 넘을 수 없습니다.")
        private String title;
        @NotBlank(message = "내용은 비어 있을 수 없습니다.") private String content;
        @NotNull(message = "카테고리ID는 필수입니다.") private Long categoryId;
    }

    // --- 응답(Response) DTOs ---
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response{
        @Schema(description = "게시글ID") private Long id;
        @Schema(description = "제목") private String title;
        @Schema(description = "내용") private String content;
        @Schema(description = "카테고리명") private String categoryName;
        @Schema(description = "생성시간") private LocalDateTime createdAt;
        @Schema(description = "수정시간") private LocalDateTime modifiedAt;
//        @Schema(description = "생성자") private Long createdBy;
//        @Schema(description = "수정자") private Long modifiedBy;
        @Schema(description = "생성자") private String createdByName;
        @Schema(description = "수정자") private String modifiedByName;

        public static Response from(Post post){
            return Response.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .categoryName(post.getCategory().getName())
                    .createdAt(post.getCreatedAt())
                    .modifiedAt(post.getModifiedAt())
                    .build();
        }
    }
}
