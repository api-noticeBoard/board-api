package com.portfolio.board.api.mapper;

import com.portfolio.board.api.dto.PostDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {
    List<PostDto.Response> searchPostByKeywordXml(@Param("keyword") String keyword);

    PostDto.Response findById(@Param("postId") Long postId);

    List<PostDto.Response> findAll();
}
