package com.portfolio.board.api.repository;

import com.portfolio.board.api.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Post 엔티티에 대한 데이터베이스 접근을 담당하는 리포지토리 인터페이스입니다.
 *
 * JpaRepository<Post, Long>를 상속받음으로써, Post 엔티티에 대한 기본적인 CRUD
 * (Create, Read, Update, Delete) 메서드(e.g., save, findById, findAll, delete)를
 * 자동으로 사용할 수 있게 됩니다.
 *
 * @Repository 어노테이션은 Spring Data JPA에서는 생략 가능합니다. JpaRepository를 상속하면
 *             자동으로 Bean으로 등록되고, JPA 예외가 Spring의 DataAccessException으로 변환됩니다.
 */
public interface PostRepository extends JpaRepository<Post, Long> {
}
