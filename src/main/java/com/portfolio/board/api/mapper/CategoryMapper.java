package com.portfolio.board.api.mapper;

import com.portfolio.board.api.dto.CategoryDto;
import com.portfolio.common.system.paging.PageDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<CategoryDto.Response> searchCategoryByKeywordXml(@Param("keyword") String keyword
            , @Param("pageRequest") PageDto.Request pageRequest);

    List<CategoryDto.FlatNode> findAllCategoriesAsFlatList();

    CategoryDto.Response findByName(@Param("categoryName") String categoryName);

    CategoryDto.Response findById(@Param("categoryId") Long categoryId);

    List<CategoryDto.Response> findAll(@Param("pageRequest") PageDto.Request pageRequest);
}
