package com.portfolio.board.api.domain;

import com.portfolio.common.business.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * '작성자' 정보를 조회하기 위한 읽기 전용 연관관계.
     * 이 필드는 BaseEntity가 상속해준 createdBy 컬럼을 사용하여 Member 엔티티와 JOIN됩니다.
     * insertable=false, updatable=false: JPA가 이 필드로 createdBy 컬럼을 수정하지 못하도록 막습니다.
     *                                  (createdBy 값은 Auditing 기능이 전담)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdBy", referencedColumnName = "id", insertable = false, updatable = false)
    private Member author; // '작성자'라는 의미의 필드 추가

    @Builder
    public Post(String title, String content){
        this.title = title;
        this.content = content;
    }
}
