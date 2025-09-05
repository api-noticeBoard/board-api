package com.portfolio.board.api.controller;

import com.portfolio.board.api.dto.PostDto;
import com.portfolio.board.api.service.PostService;
import com.portfolio.common.system.exception.BusinessException;
import com.portfolio.common.system.exception.ErrorCode;
import com.portfolio.common.system.paging.PageDto;
import com.portfolio.common.system.util.ExcelUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.List;

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
    public ResponseEntity<PostDto.Response> getPostById(@Parameter(description = "게시물 ID", example = "1")
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
     * 휴지통 게시글 목록 조회 API
     */
    @GetMapping("/trash")
    @Operation(summary = "휴지통 게시글 목록 조회", description = "내가 삭제한 게시글 목록을 페이징하여 조회합니다.")
    public ResponseEntity<PageDto.Response<PostDto.Response>> getMyDeletedPosts(@ModelAttribute PageDto.Request pageRequest) {
        PageDto.Response<PostDto.Response> deletedPosts = postService.findDeletedPosts(pageRequest);
        return ResponseEntity.ok(deletedPosts);
    }

    /**
     * 휴지통 게시글 복원 API
     */
    @PostMapping("/{postId}/restore")
    @Operation(summary = "게시글 복원", description = "휴지통의 게시글을 복원합니다. 작성자 또는 관리자만 가능합니다.")
    public ResponseEntity<Void> restorePost(@PathVariable Long postId) {
        postService.restorePost(postId);
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
        postService.softDeletePost(postId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 특정 게시글 영구 삭제 (Hard Delete)
     */
    @DeleteMapping("/{postId}/permanent")
    @Operation(summary = "게시글 영구 삭제 (관리자)", description = "게시글을 물리적으로 완전히 삭제합니다.")
    public ResponseEntity<Void> deletePostPermanently(@PathVariable Long postId) {
        // 이 API는 관리자만 호출할 수 있지만, 서비스 계층에서 한 번 더 권한 검사를 하는 것이 안전합니다.
        // 여기서는 hardDeletePost가 작성자/관리자 모두 가능하므로 그냥 호출합니다.
        postService.hardDeletePost(postId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 현재 조회한 데이터 엑셀로 다운로드.
     * @param keyword
     * @param response
     * @throws IOException
     */
    @GetMapping("/excel-download")
    public void downloadPostListAsExcel(@RequestParam(required = false) String keyword, HttpServletResponse response) throws IOException{
        // 1. 엑셀로 만들 데이터 조회
        List<PostDto.Response> postList = postService.getAllPostsForExcel(keyword);    // 페이징 없는 전체목록 조회
        // 2. 시스템 공통 유틸 호출
        ExcelUtils.downloadExcel(postList, PostDto.Response.class, "게시글_목록", response);
    }
}