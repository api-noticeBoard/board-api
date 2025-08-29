package com.portfolio.board.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.portfolio.board.api.domain.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class CategoryResponse {
    @Schema(description = "카테고리ID")
    private Long id;

    @Schema(description = "카테고리명")
    private String name;

    @Schema(description = "생성시간")
    private LocalDateTime createdAt;

    @Schema(description = "생성자")
    private Long createdBy;

    @Schema(description = "수정시간")
    private LocalDateTime modifiedAt;

    @Schema(description = "수정자")
    private Long modifiedBy;

    public CategoryResponse(Category category){
        this.id = category.getId();
        this.name = category.getName();
        this.createdAt = category.getCreatedAt();
        this.createdBy = category.getCreatedBy();
        this.modifiedAt = category.getModifiedAt();
        this.modifiedBy = category.getModifiedBy();
    }

    @Getter
    @Setter // MyBatis가 값을 주입할 수 있도록 Setter 추가
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY) // 비어있는 리스트는 JSON 응답에서 제외
    public static class CategoryTreeResponse {
        private Long id;
        private String name;
        private List<CategoryTreeResponse> children;
    }
}
