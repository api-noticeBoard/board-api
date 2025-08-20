package com.portfolio.board.api.service;

import com.portfolio.board.api.domain.Category;
import com.portfolio.board.api.domain.Post;
import com.portfolio.board.api.dto.PostRequest;
import com.portfolio.board.api.repository.CategoryRepository;
import com.portfolio.board.api.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepo;
    private final CategoryRepository categoryRepo;

    /**
     * 새로운 게시글을 생성합니다.
     *
     * @param requestDto 게시글 생성을 위한 요청 데이터.
     * @return 생성된 게시글의 고유 ID.
     * @Transactional: 이 어노테이션이 붙은 메서드는 전체가 하나의 트랜잭션 단위로 실행됩니다.
     *                메서드 실행 중 예외가 발생하면, 지금까지의 모든 데이터베이스 작업이 롤백되어
     *                데이터 일관성을 보장합니다.
     */
    @Transactional
    public Long createPost(PostRequest.Create requestDto) {
        // TODO: common에 businessException lib으로 들어오면 주석해제
        // 1. 카테고리가 존재하는지 확인합니다.
        //    orElseThrow를 사용하여 카테고리가 없으면 BusinessException을 던집니다.
        //    이 예외는 system-common의 GlobalExceptionHandler가 처리하게 됩니다.
//        Category category = categoryRepo.findById(requestDto.getCategoryId())
//                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "ID: " + requestDto.getCategoryId() + "에 해당하는 카테고리가 없습니다."));

        // 2. DTO를 Post 엔티티로 변환합니다. Builder 패턴을 사용하면 가독성이 좋습니다.
        Post newPost = Post.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
//                .category(category)
                .build();

        // 3. Post 엔티티를 저장합니다.
        //    **핵심**: 여기서 createdAt, createdBy 등을 직접 설정하지 않습니다.
        //    business-common의 BaseEntity와 AuditorAwareImpl 덕분에
        //    JPA가 DB에 저장하는 시점에 해당 값들을 자동으로 채워줍니다.
        Post savedPost = postRepo.save(newPost);

        // 4. 생성된 게시글의 ID를 반환합니다.
        return savedPost.getId();
    }
}
