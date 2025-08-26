package com.portfolio.board.api.dto;

import com.portfolio.board.api.domain.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryResponse {
    @Schema(description = "카테고리ID")
    private Long id;

    @Schema(description = "카테고리명")
    private String name;

    public CategoryResponse(Category category){
        this.id = category.getId();
        this.name = category.getName();
    }
}
