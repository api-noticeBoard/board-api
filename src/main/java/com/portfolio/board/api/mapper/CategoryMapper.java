package com.portfolio.board.api.mapper;

import com.portfolio.board.api.dto.CategoryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<CategoryDto.Response> searchCategoryByKeywordXml(@Param("keyword") String keyword);
}
