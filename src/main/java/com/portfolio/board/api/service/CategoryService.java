package com.portfolio.board.api.service;

import com.portfolio.board.api.domain.Category;
import com.portfolio.board.api.dto.CategoryRequest;
import com.portfolio.board.api.dto.CategoryResponse;
import com.portfolio.board.api.mapper.CategoryMapper;
import com.portfolio.board.api.repository.CategoryRepository;
import com.portfolio.common.system.exception.BusinessException;
import com.portfolio.common.system.exception.ErrorCode;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepo;
    private final CategoryMapper categoryMapper;
    /**
     * 카테고리 keyword 검색
     * @param keyword   검색 keyword
     * @return          카테고리 응답 데이터 리턴.
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> searchCategoryByKeywordXml(@Param("keyword") String keyword){
        return categoryMapper.searchCategoryByKeywordXml(keyword);
    }

    /**
     * 카테고리 단건 조회
     * @param name 카테고리명
     * @return     카테고리 응답 데이터 리턴.
     */
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryByName(String name){
        Category category = categoryRepo.findByName(name)
                .orElseThrow(()-> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));



        return new CategoryResponse(category);
    }

    /**
     * 카테고리 전체조회
     *
     * @return  전체 List 데이터
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllcategories(){
        return categoryRepo.findAll().stream()
                .map(CategoryResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 새로운 카테고리 생성.
     *
     * @param categoryDto 카테고리 요청 데이터.
     * @return            카테고리 ID 리턴.
     */
    @Transactional
    public Long createCategory(CategoryRequest.Create categoryDto){

        Optional<Category> category = categoryRepo.findByName(categoryDto.getName());

        if (categoryRepo.findByName(categoryDto.getName()).isPresent()) {
            throw new BusinessException(ErrorCode.CATEGORY_NAME_DUPLICATIED);
        }

        Category saveCategory = categoryRepo.save(categoryDto.toEntity());

        return saveCategory.getId();
    }

    /**
     * 카테고리 수정.
     *
     * @param categoryId    카테고리 ID.
     * @param categoryDto   카테고리 요청 데이터.
     * @return              카테고리 ID 리턴.
     */
    @Transactional
    public Long updateCategory(Long categoryId, CategoryRequest.Update categoryDto){

        // 수정할 카테고리 존재 확인
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(()-> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        // 같은 이름 카테고리 확인
        Optional<Category> newCategory = categoryRepo.findByName(categoryDto.getName());

        // 같은 카테고리명 확인
        if (newCategory.isPresent()){
            // 수정 중인 카테고리가 아닌지 확인
            if (!newCategory.get().getId().equals(categoryId)){
                // ID가 다를 경우 이름 중복
                throw new BusinessException(ErrorCode.CATEGORY_NAME_DUPLICATIED);
            }
        }
        if (categoryDto.getName().isBlank()) throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "카테고리 명을 입력해주세요.");
        category.setName(categoryDto.getName());

        return newCategory.get().getId();
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
