package com.portfolio.board.api.service;

import com.portfolio.board.api.domain.Category;
import com.portfolio.board.api.domain.Post;
import com.portfolio.board.api.dto.CategoryDto;
import com.portfolio.board.api.dto.PostDto;
import com.portfolio.board.api.repository.CategoryRepository;
import com.portfolio.board.api.repository.PostRepository;
import com.portfolio.common.system.exception.BusinessException;
import com.portfolio.common.system.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepo;
    private final CategoryRepository categoryRepo;

    public CategoryDto getCategoryById(Long id){

    }

    public CategoryDto getCategoryByName(String name){

    }

    /**
     * 전체조회
     *
     * @return  전체 List 데이터
     */
    public List<CategoryDto> getAllcategories(){
        return categoryRepo.findAll().stream()
                .map(CategoryDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 새로운 카테고리 생성.
     *
     * @param categoryDto 카테고리 요청 데이터.
     * @return            카테고리 ID 리턴.
     */
    @Transactional
    public Long createCategory(CategoryDto.Create categoryDto){

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
    public Long updateCategory(Long categoryId, CategoryDto.Update categoryDto){

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

    /**
     * 새로운 게시글을 생성합니다.
     *
     * @param postDto 게시글 생성을 위한 요청 데이터.
     * @return 생성된 게시글의 고유 ID.
     * @Transactional: 이 어노테이션이 붙은 메서드는 전체가 하나의 트랜잭션 단위로 실행됩니다.
     *                메서드 실행 중 예외가 발생하면, 지금까지의 모든 데이터베이스 작업이 롤백되어
     *                데이터 일관성을 보장합니다.
     */
    @Transactional
    public Long createPost(PostDto.Create postDto) {

        // 1. 카테고리가 존재하는지 확인합니다.
        //    orElseThrow를 사용하여 카테고리가 없으면 BusinessException을 던집니다.
        //    이 예외는 system-common의 GlobalExceptionHandler가 처리하게 됩니다.
        Category category = categoryRepo.findById(postDto.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        // 2. DTO를 Post 엔티티로 변환합니다. Builder 패턴을 사용하면 가독성이 좋습니다.
        // Builder 패턴을 DTO.toEntity에 넣음
        Post newPost = postDto.toEntity(category);

        // 3. Post 엔티티를 저장합니다.
        //    **핵심**: 여기서 createdAt, createdBy 등을 직접 설정하지 않습니다.
        //    business-common의 BaseEntity와 AuditorAwareImpl 덕분에
        //    JPA가 DB에 저장하는 시점에 해당 값들을 자동으로 채워줍니다.
        Post savedPost = postRepo.save(newPost);

        // 4. 생성된 게시글의 ID를 반환합니다.
        return savedPost.getId();
    }

    /**
     * 게시물 수정.
     *
     * @param postId    게시물 ID
     * @param postDto   게시물 요청 데이터
     * @return          게시물 ID 리턴
     */
    @Transactional
    public Long updatePost(Long postId, PostDto.Create postDto){

        // ID로 기존 게시물 존재 체크
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        // DTO 필드의 null일 경우에만 변경. Dirty Checking: 변경감지
        if (!postDto.getTitle().isBlank())      post.setTitle(postDto.getTitle());
        if (!postDto.getContent().isBlank())    post.setContent(postDto.getContent());
        if (postDto.getCategoryId() != null) {
            Category category = categoryRepo.findById(postDto.getCategoryId())
                    .orElseThrow(()-> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
            post.setCategory(category);
        }

        return post.getId();
    }

    /**
     * 게시물 삭제
     *
     * @param postId    게시물 ID
     * @return          게시물 ID 리턴.
     */
    @Transactional
    public Long deletePost(Long postId){

        // 게시글 존재 확인
        if (postRepo.existsById(postId)){
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }
        postRepo.deleteById(postId);

        return postId;
    }
}
