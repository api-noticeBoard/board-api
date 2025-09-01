package com.portfolio.board.api.service;

import com.portfolio.board.api.domain.Category;
import com.portfolio.board.api.dto.CategoryDto;
import com.portfolio.board.api.mapper.CategoryMapper;
import com.portfolio.board.api.repository.CategoryRepository;
import com.portfolio.common.system.exception.BusinessException;
import com.portfolio.common.system.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepo;
    private final CategoryMapper categoryMapper;

    /**
     * 카테고리 keyword 검색 (MyBatis)
     * @param keyword   검색 keyword
     * @return          카테고리 응답 데이터 리턴.
     */
    @Transactional(readOnly = true)
    public List<CategoryDto.Response> searchCategoryByKeywordXml(@Param("keyword") String keyword){
        return categoryMapper.searchCategoryByKeywordXml(keyword);
    }

    /**
     * 카테고리명로 조회
     * @param categoryName 카테고리명
     * @return     카테고리 응답 데이터 리턴.
     */
    @Transactional(readOnly = true)
    public CategoryDto.Response getCategoryByName(String categoryName){
        Category category = categoryRepo.findByName(categoryName)
                .orElseThrow(()-> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        return CategoryDto.Response.from(category);
    }

    /**
     * 카테고리ID로 조회
     * @param categoryId 카테고리ID
     * @return     카테고리 응답 데이터 리턴.
     */
    @Transactional(readOnly = true)
    public CategoryDto.Response getCategoryById(Long categoryId){
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(()-> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        return CategoryDto.Response.from(category);
    }

    /**
     * 카테고리 전체조회
     *
     * @return  전체 List 데이터
     */
    @Transactional(readOnly = true)
    public List<CategoryDto.Response> getAllCategories(){
        return categoryRepo.findAll().stream()
                .map(CategoryDto.Response::from)
                .collect(Collectors.toList());
    }

    /**
     * 새로운 카테고리 생성.
     *
     * @param requestDto 카테고리 요청 데이터.
     * @return            카테고리 ID 리턴.
     */
    @Transactional
    public Long createCategory(CategoryDto.CreateRequest requestDto) { // ✨ DTO 타입 수정
        // 이름 중복 검사
        if (categoryRepo.findByName(requestDto.getName()).isPresent()) {
            throw new BusinessException(ErrorCode.CATEGORY_NAME_DUPLICATIED);
        }

        // 부모 카테고리 조회
        Category parent = null;
        if (requestDto.getParentId() != null) {
            parent = categoryRepo.findById(requestDto.getParentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND, "부모 카테고리를 찾을 수 없습니다."));
        }

        Category newCategory = Category.builder()
                .name(requestDto.getName())
                .parent(parent)
                .build();

        return categoryRepo.save(newCategory).getId();
    }

    /**
     * 카테고리 수정.
     *
     * @param categoryId    카테고리 ID.
     * @param requestDto   카테고리 요청 데이터.
     * @return              카테고리 ID 리턴.
     */
    @Transactional
    public CategoryDto.Response updateCategory(Long categoryId, CategoryDto.UpdateRequest requestDto) { // ✨ DTO 타입 수정
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        // 수정하려는 이름이 이미 존재하는지 확인
        Optional<Category> existingCategory = categoryRepo.findByName(requestDto.getName());
        if (existingCategory.isPresent() && !existingCategory.get().getId().equals(categoryId)) {
            // 다른 카테고리가 이미 그 이름을 사용 중인 경우
            throw new BusinessException(ErrorCode.CATEGORY_NAME_DUPLICATIED);
        }

        // ✨ 올바른 값으로 업데이트
        category.updateName(requestDto.getName());

        // 변경된 엔티티를 DTO로 변환하여 반환
        return CategoryDto.Response.from(category);
    }

    /**
     * 카테고리 삭제
     *
     * @param categoryId 카테고리 ID
     * @return           카테고리 ID 리턴.
     */
    @Transactional
    public Long deleteCategory(Long categoryId){

        // 삭제할 카테고리 존재 확인
        if (categoryRepo.existsById(categoryId)) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        // 삭제
        categoryRepo.deleteById(categoryId);

        return categoryId;
    }
}
