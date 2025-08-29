package com.portfolio.board.api.controller;

import com.portfolio.board.api.dto.CategoryRequest;
import com.portfolio.board.api.dto.CategoryResponse;
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
    public ResponseEntity<List<CategoryResponse>> searchCategoryByKeywordXml(@Parameter(description = "keyword 검색", example = "TEST")
                                                                             @RequestParam(value = "keyword", required = false) String keyword){
        List<CategoryResponse> categoryResponses;
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
    @GetMapping("/search/{categoryId}")
    @Operation(summary = "카테고리 단건조회", description = "카테고리ID으로 단건조회")
    public ResponseEntity<CategoryResponse> getCategoryById(@Parameter(description = "카테고리ID", example = "65")
                                                              @RequestParam(value = "categoryId") Long categoryId){
        if (categoryId == null || categoryId <= 0) throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);

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
    public ResponseEntity<Void> createCategory(@Valid @RequestBody CategoryRequest.create categoryDto){
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
    @Operation(summary = "카테고리 수정", description = "카테고리ID로 수정")
    public ResponseEntity<CategoryResponse> updateCategory(@Parameter(description = "수정할 카테고리ID", example = "1")
                                               @PathVariable Long categoryId, @Valid @RequestBody CategoryRequest.update requestDto){
        CategoryResponse categoryResponse = categoryService.updateCategory(categoryId, requestDto);

        return ResponseEntity.ok().build();
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
