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

    // Post 엔티티를 DTO로 변환하는 생성자
    public PostDetailResponse(Post post) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        // ✨ 연관된 author(Member) 객체에서 이름을 꺼내 DTO 필드에 할당
        this.authorName = post.getAuthor().getName();
        this.createdAt = post.getCreatedAt();
    }

    // MyBatis용 생성자 (필요 시)
    public PostDetailResponse(Long postId, String title, String content, String authorName, LocalDateTime createdAt) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.authorName = authorName;
        this.createdAt = createdAt;
    }
}
