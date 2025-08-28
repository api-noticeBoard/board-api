package com.portfolio.board.api.domain;

import com.portfolio.common.business.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 사용자 정보를 담는 Member 엔티티 클래스.
 * BaseEntity를 상속하여 생성/수정 관련 필드를 자동으로 관리합니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA는 기본 생성자를 필요로 합니다. PROTECTED로 설정하여 무분별한 생성을 막습니다.
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB의 Auto Increment를 사용하여 ID 자동 생성
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username; // 로그인 ID로 사용될 사용자 이름

    @Column(nullable = false)
    private String password; // 암호화되어 저장될 비밀번호

    @Column(nullable = false, length = 50)
    private String name; // 실제 사용자 이름

    /**
     * 사용자의 권한 목록을 저장합니다.
     * @ElementCollection: 이 필드가 별도의 테이블(member_roles)로 관리되는 컬렉션임을 나타냅니다.
     * @CollectionTable: 생성될 테이블의 이름과 외래 키를 지정합니다.
     * @FetchType.EAGER: Member 엔티티를 조회할 때 roles 정보도 즉시 함께 가져옵니다.
     *                   (권한 정보는 항상 필요하므로 EAGER 로딩이 유리할 수 있습니다.)
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "member_roles", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name = "role")
    private List<String> roles = new ArrayList<>();

    // 빌더 패턴을 사용하여 객체를 안전하고 명확하게 생성할 수 있도록 합니다.
    @Builder
    public Member(String username, String password, String name, List<String> roles) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.roles = roles;
    }
}
