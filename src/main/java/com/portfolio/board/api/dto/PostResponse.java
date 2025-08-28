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

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PostResponse {
    @Schema(description = "게시글ID")
    private Long id;

    @Schema(description = "제목")
    private String title;

    @Schema(description = "내용")
    private String content;

    @Schema(description = "카테고리명")
    private String categoryName;

    @Schema(description = "생성시간")
    private LocalDateTime createdAt;

    @Schema(description = "생성자")
    private Long createdBy;

    @Schema(description = "수정시간")
    private LocalDateTime modifiedAt;

    @Schema(description = "수정자")
    private Long modifiedBy;

    public PostResponse(Post post){
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.categoryName = post.getCategory().getName();
        this.createdAt = post.getCreatedAt();
        this.createdBy = post.getCreatedBy();
        this.modifiedAt = post.getModifiedAt();
        this.modifiedBy = post.getModifiedBy();
    }
}
