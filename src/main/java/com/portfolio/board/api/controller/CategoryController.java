package com.portfolio.board.api.controller;

import com.portfolio.board.api.dto.CategoryResponse;
import com.portfolio.board.api.service.CategoryService;
import com.portfolio.common.system.exception.BusinessException;
import com.portfolio.common.system.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/search")
    @Operation(summary = "keyword로 카테고리 검색", description = "keyword 검색.")
    public ResponseEntity<List<CategoryResponse>> searchCategoryByKeyword(@Parameter(description = "keyword 검색", example = "TEST")
                                                                              @RequestParam(value = "keyword", required = false) String keyword){
        List<CategoryResponse> categoryResponses;
        if (keyword.isBlank()) categoryService.getAllcategories();
        if (keyword != null && !keyword.trim().isEmpty()) {
            categoryResponses = categoryService.searchCategoryByKeywordXml(keyword);
        } else {
            categoryResponses = categoryService.getAllcategories();
        }

        if (categoryResponses.isEmpty())    throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);

        return ResponseEntity.ok(categoryResponses);
    }
}
