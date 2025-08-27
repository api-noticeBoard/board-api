package com.portfolio.board.api.dto;

import com.portfolio.board.api.domain.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

public class CategoryRequest {

    @Getter @Setter
    public static class CategoryCreate{
        @NotBlank(message = "카테고리 이름은 비어 있을 수 없습니다.")
        @Size(max = 20, message = "카테고리 이름은 20자를 넘을 수 없습니다.")
        private String name;

        // DTO > Entity
        public Category toEntity(){
            return Category.builder()
                    .name(this.name)
                    .build();
        }
    }

    @Getter @Setter
    public static class CategoryUpdate{
        @NotBlank(message = "카테고리 이름은 비어 있을 수 없습니다.")
        @Size(max = 20, message = "카테고리 이름은 20자를 넘을 수 없습니다.")
        private String name;
    }

}
