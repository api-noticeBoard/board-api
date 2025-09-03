package com.portfolio.board.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.portfolio.board.api.domain.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        @Schema(description = "부모 카테고리 ID (최상위일 경우 null)", example = "97")
        private Long parentId;
    }

    /**
     * [신규] MyBatis 재귀 쿼리의 평면적인 결과를 담기 위한 내부 DTO.
     * DB에서 조회된 한 줄(row)의 데이터와 일치합니다.
     */
    @Getter
    @Setter
    public static class FlatNode {
        private Long id;
        private String name;
        private Long parentId;
        // path, level 등 재귀 쿼리에서 추가한 다른 컬럼들도 필요 시 여기에 추가할 수 있습니다.
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
//        @Schema(description = "생성자") private Long createdBy;
        @Schema(description = "수정일") private LocalDateTime modifiedAt;
//        @Schema(description = "수정자") private Long modifiedBy;
        @Schema(description = "작성자명") private String createdByName;
        @Schema(description = "수정자명") private String modifiedByName;

        public static Response from(Category category) {
            return Response.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .parentId(category.getParent() != null ? category.getParent().getId() : null)
                    .createdAt(category.getCreatedAt())
//                    .createdBy(category.getCreatedBy())
                    .modifiedAt(category.getModifiedAt())
//                    .modifiedBy(category.getModifiedBy())
                    .createdByName(category.getAuthor() != null ? category.getAuthor().getName() : null)
                    .modifiedByName(category.getModifier() != null ? category.getModifier().getName() : null)
                    .build();
        }
    }

    @Getter @ Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class TreeResponse {
        @Schema(description = "ID") private Long id;
        @Schema(description = "이름") private String name;
        /**
         * ✨ [핵심 수정] @Builder.Default 어노테이션을 추가합니다.
         * 이렇게 하면, 빌더를 통해 children 값을 설정하지 않았을 때,
         * 기본값으로 new ArrayList<>()가 사용되어 null이 되는 것을 방지합니다.
         */
        @Schema(description = "자식 목록")
        @Builder.Default
        private List<TreeResponse> children = new ArrayList<>();

        public static TreeResponse from(Category category) {
            return TreeResponse.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .children(category.getChildren().stream().map(TreeResponse::from).collect(Collectors.toList()))
                    .build();
        }
    }
}
