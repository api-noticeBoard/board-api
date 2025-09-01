package com.portfolio.board.api.controller;

import com.portfolio.board.api.dto.CategoryDto;
import com.portfolio.board.api.service.CategoryService;
import com.portfolio.common.system.exception.BusinessException;
import com.portfolio.common.system.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Category", description = "카테고리 관련 API")
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * keywod 검색
     *
     * @param keyword 검색할 키워드
     * @return        응답 데이터 리턴.
     */
    @GetMapping("/search")
    @Operation(summary = "keyword로 카테고리 검색", description = "keyword 검색")
    public ResponseEntity<List<CategoryDto.Response>> searchCategoryByKeywordXml(@Parameter(description = "keyword 검색", example = "TEST")
                                                                             @RequestParam(value = "keyword", required = false) String keyword){
        List<CategoryDto.Response> categoryResponses;
        if (keyword != null && !keyword.trim().isEmpty()) {
            categoryResponses = categoryService.searchCategoryByKeywordXml(keyword);
        } else {
            categoryResponses = categoryService.getAllCategories();
        }
        if (categoryResponses.isEmpty())    throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);

        return ResponseEntity.ok(categoryResponses);
    }

    /**
     * 카테고리 ID로 단건조회
     * @param categoryId 카테고리ID
     * @return           카테고리 응답 데이터 리턴.
     */
    @GetMapping("/{categoryId}")
    @Operation(summary = "카테고리 단건 조회", description = "카테고리 ID로 단건 조회")
    public ResponseEntity<CategoryDto.Response> getCategoryById(@PathVariable Long categoryId) { // ✨ DTO 타입 수정
        return ResponseEntity.ok(categoryService.getCategoryById(categoryId));
    }

    /**
     * 카테고리 생성
     *
     * @param categoryDto 카테고리 요청 데이터
     * @return           카테고리 생성된 URI 리턴.
     */
    @PostMapping
    @Operation(summary = "카테고리 생성", description = "새로운 카테고리 생성")
    public ResponseEntity<Void> createCategory(@Valid @RequestBody CategoryDto.CreateRequest categoryDto){
        Long categoryId = categoryService.createCategory(categoryDto);

        // RESTful API 원칙에 따라, 생성된 리소스에 접근할 수 있는 URI를
        // Location 헤더에 담아 201 Created 상태 코드로 응답합니다.
        URI location = URI.create("/api/v1/categories/" + categoryId);
        return ResponseEntity.created(location).build();
    }

    /**
     * 카테고리 수정
     *
     * @param categoryId 카테고리ID
     * @param requestDto 카테고리 수정 데이터
     * @return           Http 상태 리턴
     */
    @PutMapping("/{categoryId}")
    @Operation(summary = "카테고리 수정", description = "기존 카테고리의 이름을 수정합니다.")
    public ResponseEntity<CategoryDto.Response> updateCategory( // ✨ DTO 타입 수정
                                                                @Parameter(description = "수정할 카테고리ID", example = "1") @PathVariable Long categoryId,
                                                                @Valid @RequestBody CategoryDto.UpdateRequest requestDto) { // ✨ DTO 타입 수정
        CategoryDto.Response updatedCategory = categoryService.updateCategory(categoryId, requestDto);
        return ResponseEntity.ok(updatedCategory);
    }

    /**
     *카테고리 삭제
     *
     * @param categoryId 카테고리ID
     * @return           Http 상태 리턴.
     */
    @DeleteMapping("/{categoryId}")
    @Operation(summary = "카테고리 삭제", description = "카테고리ID로 삭제")
    public ResponseEntity<Void> deleteCategory(@Parameter(description = "삭제할 카테고리ID", example = "1")
                                               @PathVariable Long categoryId){
        categoryService.deleteCategory(categoryId);

        return ResponseEntity.noContent().build();
    }
}
