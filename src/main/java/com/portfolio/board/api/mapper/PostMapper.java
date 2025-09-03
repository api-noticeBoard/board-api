package com.portfolio.board.api.mapper;

import com.portfolio.board.api.dto.PostDto;
import com.portfolio.common.system.paging.PageDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PostMapper {
    List<PostDto.Response> searchPostByKeywordXml(
            @Param("keyword") String keyword
            , @Param("pageRequest") PageDto.Request pageRequest
    );

    Optional<PostDto.Response> findById(@Param("postId") Long postId);

    List<PostDto.Response> findAll(@Param("pageRequest") PageDto.Request pageRequest); // ✨ 페이징 객체 명시
}
