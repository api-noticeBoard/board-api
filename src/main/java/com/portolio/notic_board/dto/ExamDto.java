package com.portolio.notic_board.dto;

import com.portolio.notic_board.entity.ExamEntity;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor // 생성자 에러 해결을 위해 추가
@AllArgsConstructor // 생성자 에러 해결을 위해 추가
public class ExamDto {
    private Long id;
    private String title;
    private String content;
    private String author;

    /**
     * Entity를 DTO로 변환하는 생성자
     * @param entity 변환할 ExamEntity 객체
     */
    public ExamDto(ExamEntity entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.author = entity.getAuthor();
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
