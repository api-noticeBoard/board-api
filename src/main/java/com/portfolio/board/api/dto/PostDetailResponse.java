package com.portfolio.board.api.dto;

import com.portfolio.board.api.domain.Post;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 게시글 상세 조회 시 클라이언트에게 반환될 데이터를 담는 DTO.
 */
@Getter
public class PostDetailResponse {
    //TODO: 정리해서 다시 만들기
    private final Long postId;
    private final String title;
    private final String content;
    private final String authorName; // ✨ 사용자 ID(Long)가 아닌, 사용자 이름(String)
    private final LocalDateTime createdAt;

    // ✨ 이제 이 생성자 하나만으로 JPA와 MyBatis 모두를 커버할 수 있습니다.
    public PostDetailResponse(Post post) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        // author가 null일 수 있는 경우(LEFT JOIN)를 대비하여 null 체크 추가
        this.authorName = (post.getAuthor() != null) ? post.getAuthor().getName() : "작성자 없음";
        this.createdAt = post.getCreatedAt();
    }
}
