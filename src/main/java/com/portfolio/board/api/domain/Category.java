package com.portfolio.board.api.domain;

import com.portfolio.common.business.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자만 남깁니다.
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdBy", referencedColumnName = "id", insertable = false, updatable = false)
    private Member author; // '작성자'라는 의미의 필드 추가

    /**
     * Category 엔티티를 생성하기 위한 정적 팩토리 메서드입니다.
     * 생성 시점에는 'name'만 필요하다는 비즈니스 규칙을 명확히 합니다.
     * @param name 생성할 카테고리의 이름
     * @return name 필드가 설정된 새로운 Category 객체
     */
    public static Category create(String name) {
        Category category = new Category();
        category.name = name;
        return category;
    }

    /**
     * 카테고리 이름을 수정하는 비즈니스 메서드입니다.
     * Setter를 직접 노출하는 대신, 의도가 명확한 메서드를 제공합니다.
     * @param newName 변경할 새로운 카테고리 이름
     */
    public void updateName(String newName) {
        this.name = newName;
    }
}
