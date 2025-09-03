package com.portfolio.board.config;

import com.portfolio.common.system.paging.PagingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis 관련 설정을 구성하는 클래스.
 */
@Configuration
public class MyBatisconfig {

    /**
     * 우리가 만든 PagingInterceptor를 Spring의 빈으로 등록합니다.
     * 이렇게 등록된 인터셉터는 MyBatis가 초기화될 때 자동으로 적용됩니다.
     */
    @Bean
    public PagingInterceptor pagingInterceptor(){
        return new PagingInterceptor();
    }
}
