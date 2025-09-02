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

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepo;
    private final CategoryMapper categoryMapper;

    /**
     * [최종 권장 방식] MyBatis 재귀 쿼리를 사용하여 전체 카테고리 목록을 계층 구조로 조회합니다.
     * DB 호출은 단 한 번으로, 애플리케이션 메모리에서 트리 구조를 조립하여 성능이 매우 좋습니다.
     *
     * @return TreeResponse
     */
    @Transactional(readOnly = true)
    public List<CategoryDto.TreeResponse> getCategoryTree() {
//        List<Category> allCategories = categoryRepo.findAll();
//
//        return allCategories.stream()
//                .filter(c -> c.getParent() == null) // 최상위 카테고리만 필터링
//                .map(CategoryDto.TreeResponse::from) // 재귀적으로 DTO 변환 시작
//                .collect(Collectors.toList());

        // MyBatis 방식 예시
         List<CategoryDto.FlatNode> flatList = categoryMapper.findAllCategoriesAsFlatList();

         return buildTreeFromFlatList(flatList);
    }

    /**
     * * 평면적인 노드 리스트를 계층적인 트리 구조로 변환하는 헬퍼 메서드.
     * @param flatList DB에서 조회된 평면적인 카테고리 DTO 리스트
     * @return 계층 구조로 조립된 최상위 카테고리 DTO 리스트
     *
     * @param flatList
     * @return TreeResponse
     */
    private List<CategoryDto.TreeResponse> buildTreeFromFlatList(List<CategoryDto.FlatNode> flatList) {
        // 최종적으로 반환될 최상위 노드 리스트
        List<CategoryDto.TreeResponse> rootNodes = new ArrayList<>();

        // 각 노드를 빠르게 찾기 위해 ID를 key로 사용하는 Map을 생성합니다. (성능 최적화)
        Map<Long, CategoryDto.TreeResponse> nodeMap = new HashMap<>();

        // 1단계: 모든 노드를 TreeResponse DTO로 변환하고 Map에 저장합니다.
        for (CategoryDto.FlatNode flatNode : flatList) {
            CategoryDto.TreeResponse treeNode = new CategoryDto.TreeResponse();
            treeNode.setId(flatNode.getId());
            treeNode.setName(flatNode.getName());
            nodeMap.put(treeNode.getId(), treeNode);
        }

        // 2단계: 각 노드를 순회하면서 자신의 부모를 찾아 연결합니다.
        for (CategoryDto.FlatNode flatNode : flatList) {
            Long parentId = flatNode.getParentId();
            // value값인 TreeResponse 주입
            CategoryDto.TreeResponse currentNode = nodeMap.get(flatNode.getId());

            if (parentId == null) {
                // 부모 ID가 null이면 최상위 노드이므로, rootNodes 리스트에 추가합니다.
                rootNodes.add(currentNode);
            } else {
                // 부모 ID가 있으면, Map에서 부모 노드를 찾습니다.
                // value값인 TreeResponse 주입 결국 parentNode == currentNode == nodeMap.get()
                CategoryDto.TreeResponse parentNode = nodeMap.get(parentId);
                if (parentNode != null) {
                    // 부모 노드의 children 리스트에 현재 노드를 추가합니다.
                    parentNode.getChildren().add(currentNode);
                }
            }
        }

        return rootNodes;
    }

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
        // JPA
//        Category category = categoryRepo.findByName(categoryName)
//                .orElseThrow(()-> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
//
//        return CategoryDto.Response.from(category);
        // MyBatis
        return categoryMapper.findByName(categoryName);
    }

    /**
     * 카테고리ID로 조회
     * @param categoryId 카테고리ID
     * @return     카테고리 응답 데이터 리턴.
     */
    @Transactional(readOnly = true)
    public CategoryDto.Response getCategoryById(Long categoryId){
        // JPA
//        Category category = categoryRepo.findById(categoryId)
//                .orElseThrow(()-> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
//
//        return CategoryDto.Response.from(category);
        // MyBatis
        return categoryMapper.findById(categoryId);
    }

    /**
     * 카테고리 전체조회
     *
     * @return  전체 List 데이터
     */
    @Transactional(readOnly = true)
    public List<CategoryDto.Response> getAllCategories(){
        // JPA
//        return categoryRepo.findAll().stream()
//                .map(CategoryDto.Response::from)
//                .collect(Collectors.toList());
        return categoryMapper.findAll();
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
            throw new BusinessException(ErrorCode.CATEGORY_DUPLICATIED);
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
        // 수정할 카테고리 조회
        Category categoryToUpdate = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        // 수정하려는 이름이 이미 존재하는지 확인
        Optional<Category> existingCategory = categoryRepo.findByName(requestDto.getName());
        if (existingCategory.isPresent() && !existingCategory.get().getId().equals(categoryId)) {
            // 다른 카테고리가 이미 그 이름을 사용 중인 경우
            throw new BusinessException(ErrorCode.CATEGORY_DUPLICATIED);
        }
        // ✨ 올바른 값으로 업데이트
        categoryToUpdate.updateName(requestDto.getName());

        updateParentCategory(categoryToUpdate, requestDto.getParentId());

        // 변경된 엔티티를 DTO로 변환하여 반환
        return CategoryDto.Response.from(categoryToUpdate);
    }

    /**
     * 부모 카테고리를 변경하는 로직을 담당하는 private 헬퍼 메서드.
     *
     * @param categoryToUpdate  변경 대상 카테고리 엔티티
     * @param newParentId       새로운 부모 카테고리 ID (null일 경우 최상위로 변경)
     */
    private void updateParentCategory(Category categoryToUpdate, Long newParentId) {
        // 현재 부모 ID를 가져옵니다. (null일 수 있음)
        Long currentParentId = (categoryToUpdate.getParent() != null) ? categoryToUpdate.getParent().getId() : null;

        // 새로운 부모 ID와 현재 부모 ID가 같으면 변경할 필요가 없으므로 로직을 종료합니다.
        if (Objects.equals(currentParentId, newParentId)) {
            throw new BusinessException(ErrorCode.CATEGORY_DUPLICATIED);
        }

        // 새로운 부모 카테고리를 설정합니다.
        if (newParentId != null) {
            // (3-1) 새로운 부모가 될 카테고리를 DB에서 조회합니다.
            Category newParent = categoryRepo.findById(newParentId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

            // (3-2) ✨ [중요] 순환 참조 방지 검사
            validateCircularDependency(categoryToUpdate, newParent);

            // (3-3) 검사를 통과하면, 새로운 부모를 설정합니다.
            categoryToUpdate.changeParent(newParent);
        } else {
            // (3-4) newParentId가 null이면, 최상위 카테고리로 변경하는 것이므로 부모를 null로 설정합니다.
            categoryToUpdate.changeParent(null);
        }
    }

    /**
     * 순환 참조가 발생하는지 검증하는 메서드.
     * (A의 새 부모가 A 자신이거나, A의 하위 카테고리인지 확인)
     */
    private void validateCircularDependency(Category source, Category targetParent) {
        // (1) 자기 자신을 부모로 지정하려는 경우
        if (source.getId().equals(targetParent.getId())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "자기 자신을 부모 카테고리로 지정할 수 없습니다.");
        }

        // (2) 자신의 하위 카테고리를 부모로 지정하려는 경우
        Category current = targetParent;
        while (current != null) {
            if (current.getId().equals(source.getId())) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "자신의 하위 카테고리를 부모로 지정할 수 없습니다.");
            }
            // 부모를 따라 계속 올라갑니다.
            current = current.getParent();
        }
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
