package com.portfolio.board.api.controller;

import com.portfolio.board.api.dto.PostRequest;
import com.portfolio.board.api.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * POST /api/v1/posts
     * 새로운 게시글을 생성하는 API 엔드포인트입니다.
     *
     * @param requestDto 클라이언트로부터 받은 게시글 생성 요청 데이터 (DTO).
     *                   @Valid 어노테이션을 통해 DTO에 정의된 유효성 검증 규칙이 자동으로 적용됩니다.
     *                   @RequestBody 어노테이션은 HTTP 요청의 본문을 자바 객체로 변환해줍니다.
     * @return 생성 성공 시, HTTP 상태 코드 201 Created와 함께
     *         생성된 리소스의 위치를 나타내는 'Location' 헤더를 반환합니다.
     */
    @PostMapping
    public ResponseEntity<Void> createPost(@Valid @RequestBody PostRequest.Create requestDto) {
        // 1. @Valid 어노테이션을 통해 requestDto의 유효성을 검사합니다.
        //    만약 검증에 실패하면, system-common의 GlobalExceptionHandler가
        //    MethodArgumentNotValidException을 처리하여 400 에러를 응답합니다.

        // 2. Service를 호출하여 비즈니스 로직을 수행하고, 생성된 게시글의 ID를 받습니다.
        Long postId = postService.createPost(requestDto);

        // 3. RESTful API 원칙에 따라, 생성된 리소스에 접근할 수 있는 URI를
        //    Location 헤더에 담아 201 Created 상태 코드로 응답합니다.
        URI location = URI.create("/api/v1/posts/" + postId);
        return ResponseEntity.created(location).build();
    }
}