package com.portfolio.board.api.mapper;

import com.portfolio.board.api.dto.CategoryResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<CategoryResponse> searchCategoryByKeywordXml(@Param("keyword") String keyword);
}
