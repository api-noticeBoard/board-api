package com.portfolio.board.api.controller;

import com.portfolio.board.api.dto.PostDto;
import com.portfolio.board.api.service.PostService;
import com.portfolio.common.system.exception.BusinessException;
import com.portfolio.common.system.exception.ErrorCode;
import com.portfolio.common.system.paging.PageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@Tag(name = "Post", description = "게시글 관련 API")
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * keywod 검색
     *
     * @param keyword 검색할 키워드
     * @return        응답 데이터 리턴.
     */
    @GetMapping("/search")
    @Operation(summary = "keyword 검색", description = "게시글을 keyword로 검색")
    public ResponseEntity<PageDto.Response<PostDto.Response>> searchPostByKeywordXml(@Parameter(description = "keyword 검색", example = "테스트")
                                                                     @RequestParam(value = "keyword", required = false) String keyword,
                                                                     @Parameter(description = "페이지 요청 정보")
                                                                     @ModelAttribute PageDto.Request pageRequest){
        PageDto.Response<PostDto.Response> response =
                postService.searchPostByKeywordXml(keyword, pageRequest);

        if (response.getContent().isEmpty())
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);

        return ResponseEntity.ok(response);
    }

    /**
     * 게시물 단건조회
     *
     * @param postId 게시물ID
     * @return       응답 데이터 리턴
     */
    @GetMapping("/search/{postId}")
    @Operation(summary = "게시물 단건조회", description = "게시글")
    public ResponseEntity<Optional<PostDto.Response>> getPostById(@Parameter(description = "게시물 ID", example = "1")
                                                        @PathVariable Long postId){
        if (postId == null || postId <= 0) throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);

        return ResponseEntity.ok(postService.getPostById(postId));
    }

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
    @Operation(summary = "게시글 생성", description = "새로운 게시글 생성")
    public ResponseEntity<Void> createPost(@Valid @RequestBody PostDto.CreateRequest requestDto) {
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

    /**
     * 게시글 수정
     *
     * @param postId     게시글 ID
     * @param requestDto 게시글 요청 내용
     * @return           Http 상태 리턴.
     */
    @PutMapping("/{postId}")
    @Operation(summary = "게시글 수정", description = "게시글ID로 수정")
    public ResponseEntity<Void> updatePost(@Parameter(description = "수정할 ID", example = "1")
                                               @PathVariable Long postId, @Valid @RequestBody PostDto.UpdateRequest requestDto){
        postService.updatePost(postId, requestDto);

        return ResponseEntity.ok().build();
    }

    /**
     * 게시글 삭제
     *
     * @param postId    게시글 ID
     * @return          Http 상태 리턴.
     */
    @DeleteMapping("/{postId}")
    @Operation(summary = "게시글 삭제", description = "게시글ID의 삭제")
    public ResponseEntity<Void> deletePost(@Parameter(description = "삭제할 ID", example = "1")
                                           @PathVariable Long postId){
        postService.deletePost(postId);

        return ResponseEntity.noContent().build();
    }

}