package com.portfolio.board.api.mapper;

import com.portfolio.board.api.dto.PostResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {
    List<PostResponse> searchPostByKeywordXml(@Param("keyword") String keyword);
}
