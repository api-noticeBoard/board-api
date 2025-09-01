package com.portfolio.board.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.portfolio.board.api.domain.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 카테고리와 관련된 모든 DTO(Data Transfer Object)를 관리하는 클래스.
 * 내부 정적 클래스로 요청(Request)과 응답(Response) DTO를 구분합니다.
 */
public class CategoryDto {

    // --- 요청(Request) DTOs ---

    @Getter
    @Setter
    public static class CreateRequest {
        @NotBlank(message = "카테고리 이름은 필수입니다.")
        @Size(max = 20, message = "이름은 20자를 넘을 수 없습니다.")
        @Schema(description = "생성할 카테고리 이름", example = "Spring Boot")
        private String name;

        @Schema(description = "부모 카테고리 ID (최상위일 경우 null)", example = "1")
        private Long parentId;
    }

    @Getter @Setter
    public static class UpdateRequest {
        @NotBlank(message = "카테고리 이름은 필수입니다.")
        @Size(max = 20, message = "이름은 20자를 넘을 수 없습니다.")
        @Schema(description = "수정할 카테고리 이름", example = "JPA")
        private String name;
    }

    // --- 응답(Response) DTOs ---

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        @Schema(description = "ID") private Long id;
        @Schema(description = "이름") private String name;
        @Schema(description = "부모 ID") private Long parentId;
        @Schema(description = "생성일") private LocalDateTime createdAt;
        @Schema(description = "생성자") private Long createdBy;
        @Schema(description = "수정일") private LocalDateTime modifiedAt;
        @Schema(description = "수정자") private Long modifiedBy;

        public static Response from(Category category) {
            return Response.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .parentId(category.getParent() != null ? category.getParent().getId() : null)
                    .createdAt(category.getCreatedAt())
                    .createdBy(category.getCreatedBy())
                    .modifiedAt(category.getModifiedAt())
                    .modifiedBy(category.getModifiedBy())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class TreeResponse {
        @Schema(description = "ID") private Long id;
        @Schema(description = "이름") private String name;
        @Schema(description = "자식 목록") private List<TreeResponse> children;

        public static TreeResponse from(Category category) {
            return TreeResponse.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .children(category.getChildren().stream().map(TreeResponse::from).collect(Collectors.toList()))
                    .build();
        }
    }
}
