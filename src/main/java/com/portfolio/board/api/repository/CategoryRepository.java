package com.portfolio.board.api.repository;

import com.portfolio.board.api.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
