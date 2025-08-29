package com.portfolio.board.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

public class CategoryRequest {

    /**
     * 카테고리 생성을 위한 요청 DTO.
     * @Getter: 모든 필드의 getter 메서드를 생성합니다.
     * @Setter: 모든 필드의 setter 메서드를 생성합니다. (JSON 바인딩을 위해 필요)
     * @NoArgsConstructor: 파라미터 없는 기본 생성자를 생성합니다. (JSON 바인딩을 위해 필요)
     * @AllArgsConstructor: 모든 필드를 포함하는 생성자를 생성합니다. (Builder가 사용하기 위해 필요)
     * @Builder: 빌더 패턴 코드를 자동으로 생성합니다.
     */
    @Getter
    @Setter // Spring의 JSON 바인딩을 위해 Setter도 추가
    @NoArgsConstructor // 기본 생성자 추가
    @AllArgsConstructor // Builder를 위한 전체 필드 생성자 추가
    @Builder
    public static class CategoryCreate {
        @NotBlank(message = "카테고리 이름은 비어 있을 수 없습니다.")
        @Size(max = 20, message = "카테고리 이름은 20자를 넘을 수 없습니다.")
        private String name;
    }

    @Getter @Setter
    public static class CategoryUpdate{
        @NotBlank(message = "카테고리 이름은 비어 있을 수 없습니다.")
        @Size(max = 20, message = "카테고리 이름은 20자를 넘을 수 없습니다.")
        private String name;
    }

}
