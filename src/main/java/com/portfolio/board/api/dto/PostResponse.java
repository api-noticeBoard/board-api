package com.portfolio.board.api.dto;

import com.portfolio.board.api.domain.Category;
import com.portfolio.board.api.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class PostResponse {
    @Schema(description = "카테고리명")
    private Long id;
    @Schema(description = "카테고리명")
    private String title;
    @Schema(description = "카테고리명")
    private String content;
    @Schema(description = "카테고리명")
    private String categoryName;

    public PostResponse(Post post){
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.categoryName = post.getCategory().getName();
    }
}
