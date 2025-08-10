package com.portfolio.board.exam.dto;

import com.portfolio.board.exam.entity.ExamEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Schema(description = "게시물 정보 DTO")
@Getter
@Setter
@Builder
@NoArgsConstructor // 생성자 에러 해결을 위해 추가
@AllArgsConstructor // 생성자 에러 해결을 위해 추가
public class ExamDto {

    @Schema(description = "사용자 고유 ID", example = "1")
    private Long id;
    @Schema(description = "게시물 제목", example = "프로젝트1")
    private String title;
    @Schema(description = "게시물 내용", example = "프로젝트 내용")
    private String content;
    @Schema(description = "게시물 작성자", example = "김철수")
    private String author;

    @Schema(description = "생성일", example = "2025-08-07T12:05:59.673852", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
    @Schema(description = "수정일", example = "2025-08-07T12:15:59.673852", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;

    /**
     * Entity를 DTO로 변환하는 생성자
     * @param entity 변환할 ExamEntity 객체
     */
    public ExamDto(ExamEntity entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.author = entity.getAuthor();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
    }

    /**
     * DTO를 Entity로 변환하는 메서드
     * @return 변환된 ExamEntity 객체
     */
    public ExamEntity toEntity() {
        return ExamEntity.builder()
                .id(this.id) // update 시 id가 필요할 수 있음
                .title(this.title)
                .content(this.content)
                .author(this.author)
                .build();
    }
}
