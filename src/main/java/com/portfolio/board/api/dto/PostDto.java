package com.portfolio.board.api.dto;

import com.portfolio.board.api.domain.Post;
import com.portfolio.common.system.util.ExcelColumn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

public class PostDto {

    // --- 요청(Request) DTOs ---
    @Getter
    @Setter
    @Schema(name = "PostCreateRequest", description = "게시글 생성을 위한 DTO")
    public static class CreateRequest {
        @NotBlank(message = "제목은 비어 있을 수 없습니다.")
        @Size(max = 100, message = "제목은 100자를 넘을 수 없습니다.")
        private String title;
        @NotBlank(message = "내용은 비어 있을 수 없습니다.")
        private String content;
        @NotNull(message = "카테고리ID는 필수입니다.")
        private Long categoryId;
    }

    @Getter
    @Setter
    @Schema(name = "PostUpdateRequest", description = "게시글 수정을 위한 DTO")
    public static class UpdateRequest {
        @NotBlank(message = "제목은 비어 있을 수 없습니다.")
        @Size(max = 100, message = "제목은 100자를 넘을 수 없습니다.")
        private String title;
        @NotBlank(message = "내용은 비어 있을 수 없습니다.")
        private String content;
        @NotNull(message = "카테고리ID는 필수입니다.")
        private Long categoryId;
    }

    // --- 응답(Response) DTOs ---
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        @ExcelColumn(headerName = "ID", order = 1) // ✨ 엑셀 헤더 정보 추가
        @Schema(description = "게시글ID")
        private Long id;
        @ExcelColumn(headerName = "제목", order = 2)
        @Schema(description = "제목")
        private String title;
        // content는 내용이 길 수 있으므로 엑셀 다운로드에서 제외 (어노테이션 없음)
        @Schema(description = "내용")
        private String content;
        @ExcelColumn(headerName = "카테고리", order = 3)
        @Schema(description = "카테고리명")
        private String categoryName;
        @ExcelColumn(headerName = "작성일", order = 6)
        @Schema(description = "생성시간")
        private LocalDateTime createdAt;
        @ExcelColumn(headerName = "수정일", order = 7)
        @Schema(description = "수정시간")
        private LocalDateTime modifiedAt;
        //        @Schema(description = "생성자") private Long createdBy;
//        @Schema(description = "수정자") private Long modifiedBy;
        @ExcelColumn(headerName = "작성자", order = 4)
        @Schema(description = "생성자")
        private String createdByName;
        @ExcelColumn(headerName = "수정자", order = 5)
        @Schema(description = "수정자")
        private String modifiedByName;

        public static Response from(Post post) {
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

    @Getter
    @Setter@NoArgsConstructor
    public static class UploadRequest{
        @ExcelColumn(colIndex = 0)  // A열
        private String title;
        @ExcelColumn(colIndex = 1)  // B열
        private String content;
        @ExcelColumn(colIndex = 2)  // C열
        private Long categoryId;

    }
}
