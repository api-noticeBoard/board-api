package com.portfolio.board.api.domain;

import com.portfolio.common.business.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자만 남깁니다.
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    // --- 자기 참조 연관관계 ---

    /**
     * 부모 카테고리를 참조합니다. (다대일 관계)
     * 여러 자식(Many)은 하나의 부모(One)를 가집니다.
     * @ManyToOne: 다대일 관계를 나타냅니다.
     * @JoinColumn: 외래 키(FK) 컬럼을 지정합니다. 이름은 'parent_id'.
     * fetch = FetchType.LAZY: 부모 카테고리는 필요할 때만 조회하여 성능을 최적화합니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    /**
     * 자식 카테고리 목록을 참조합니다. (일대다 관계)
     * 하나의 부모(One)는 여러 자식(Many)을 가질 수 있습니다.
     * @OneToMany: 일대다 관계를 나타냅니다.
     * mappedBy = "parent": 이 관계는 Category 엔티티의 'parent' 필드에 의해 매핑되었음을 나타냅니다.
     *                      (자식 테이블이 외래 키를 관리하도록 함)
     */
    @OneToMany(mappedBy = "parent")
    private List<Category> children = new ArrayList<>();


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdBy", referencedColumnName = "id", insertable = false, updatable = false)
    private Member author; // '작성자'라는 의미의 필드 추가

    @Builder
    public Category(String name, Category parent){
        this.name = name;
        this.parent = parent;
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
