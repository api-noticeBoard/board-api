package com.portfolio.board.api.service;

import com.portfolio.board.api.domain.Category;
import com.portfolio.board.api.domain.Post;
import com.portfolio.board.api.dto.PostDto;
import com.portfolio.board.api.mapper.PostMapper;
import com.portfolio.board.api.repository.CategoryRepository;
import com.portfolio.board.api.repository.PostRepository;
import com.portfolio.common.business.user.AuthUser;
import com.portfolio.common.business.user.UserInfoHolder;
import com.portfolio.common.system.exception.BusinessException;
import com.portfolio.common.system.exception.ErrorCode;
import com.portfolio.common.system.paging.PageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepo;
    private final CategoryRepository categoryRepo;

    private final PostMapper postMapper;


    /**
     * 게시글 keyword 검색
     *
     * @param keyword 검색 keyword
     * @return 카테고리 응답 데이터 리턴.
     */
    @Transactional(readOnly = true)
    public PageDto.Response<PostDto.Response> searchPostByKeywordXml(@Param("keyword") String keyword, PageDto.Request pageRequest) {
        // 1. 매퍼 호출 -> 인터셉터가 페이징 처리 및 pageRequest.totalCount 설정
        List<PostDto.Response> content = postMapper.searchPostByKeywordXml(keyword, pageRequest);
        // 2. 결과 조합하여 반환
        return new PageDto.Response<>(content, pageRequest);
    }

    /**
     * 게시물ID로 단건조회
     *
     * @param postId 게시물ID
     * @return 응답 데이터 리턴.
     */
    @Transactional(readOnly = true)
    public PostDto.Response getPostById(@Param("postId") Long postId) {
        // JPA
//        Post post = postRepo.findById(postId)
//                .orElseThrow(()-> new BusinessException(ErrorCode.POST_NOT_FOUND));
//
//        return new PostResponse(post);
        // MyBatis
        Optional<PostDto.Response> response = postMapper.findById(postId);

        return response.orElseThrow(()-> new BusinessException(ErrorCode.POST_NOT_FOUND));
    }


    /**
     * 새로운 게시글을 생성합니다.
     *
     * @param postDto 게시글 생성을 위한 요청 데이터.
     * @return 생성된 게시글의 고유 ID.
     * @Transactional 이 어노테이션이 붙은 메서드는 전체가 하나의 트랜잭션 단위로 실행됩니다.
     * 메서드 실행 중 예외가 발생하면, 지금까지의 모든 데이터베이스 작업이 롤백되어
     * 데이터 일관성을 보장합니다.
     */
    @Transactional
    public Long createPost(PostDto.CreateRequest postDto) {

        // 1. 카테고리가 존재하는지 확인합니다.
        //    orElseThrow를 사용하여 카테고리가 없으면 BusinessException을 던집니다.
        //    이 예외는 system-common의 GlobalExceptionHandler가 처리하게 됩니다.
        Category category = categoryRepo.findById(postDto.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        // 2. DTO를 Post 엔티티로 변환합니다. Builder 패턴을 사용하면 가독성이 좋습니다.
        // Builder 패턴을 DTO.toEntity에 넣음
//        Post newPost = postDto.toEntity(category);
        Post newPost = Post.builder()
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .category(category)
                .build();

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
     * @param postId  게시물 ID
     * @param postDto 게시물 요청 데이터
     * @return 게시물 ID 리턴
     */
    @Transactional
    public Long updatePost(Long postId, PostDto.UpdateRequest postDto) {

        // ID로 기존 게시물 존재 체크
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        // DTO 필드의 null일 경우에만 변경. Dirty Checking: 변경감지
        if (!postDto.getTitle().isBlank()) post.setTitle(postDto.getTitle());
        if (!postDto.getContent().isBlank()) post.setContent(postDto.getContent());
        if (postDto.getCategoryId() != null) {
            Category category = categoryRepo.findById(postDto.getCategoryId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
            post.setCategory(category);
        }

        return post.getId();
    }

    /**
     * 게시물 soft 삭제
     *
     * @param postId
     */
    @Transactional
    public void softDeletePost(Long postId) {
        AuthUser currentUser = UserInfoHolder.getAuthUser(); // 현재 로그인 정보
        Post post = postRepo.findById(postId)
                .orElseThrow(()-> new BusinessException(ErrorCode.POST_NOT_FOUND));

        // 관리자 및 작성자만 삭제 가능
        if (!isAuthorOrAdmin(post, currentUser)){
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        // setDeletedAt
        post.softDelete();
    }

    @Transactional
    public void hardDeletePost(Long postId) {
        AuthUser currentUser = UserInfoHolder.getAuthUser();
        // ✨ Hard Delete는 삭제된 게시글도 대상이 될 수 있으므로, JPA 기본 findById를 우회해야 함
        // 여기서는 우선 삭제되지 않은 게시글만 영구 삭제한다고 가정.
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        // ✨ 권한 검사: 관리자이거나, 게시글 작성자인 경우에만 영구 삭제 가능
        if (!isAuthorOrAdmin(post, currentUser)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        postRepo.delete(post); // 실제 DELETE 쿼리 실행
    }

    @Transactional
    public void restorePost(Long postId) {
        AuthUser currentUser = UserInfoHolder.getAuthUser();
        Post post = postRepo.findById(postId)
                .orElseThrow(()-> new BusinessException(ErrorCode.POST_NOT_FOUND));
        // 관리자 및 작성자만 복구 가능
        if (!isAuthorOrAdmin(post, currentUser)){
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        // setDeletedAt
        post.restore();
    }

    /**
     * 삭제된 게시글 조회
     *
     * @param pageRequest
     * @return
     */
    @Transactional(readOnly = true)
    public PageDto.Response<PostDto.Response> findDeletedPosts(PageDto.Request pageRequest) {
        String currentUserName = UserInfoHolder.getUsername();
        List<PostDto.Response> deletedPostPage = postMapper.findDeletedPosts(currentUserName, pageRequest);

        return new PageDto.Response<>(deletedPostPage, pageRequest);
    }

    /**
     * 유저 및 관리자 판별 help method
     * @param post
     * @param user
     * @return
     */
    private boolean isAuthorOrAdmin(Post post, AuthUser user){
        boolean isAdmin = user.roles().contains("ROLE_ADMIN");
        boolean isAuthor = post.getCreatedBy().equals(user.userId());

        return isAdmin || isAuthor;
    }


}
