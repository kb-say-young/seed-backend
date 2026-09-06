package com.sayyoung.seed.global.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuerydslConfig {

    /**
     * QueryDSL에서 JPQL 쿼리를 생성하고 실행할 때 사용하는 객체.
     * <p>
     * Spring이 관리하는 EntityManager를 주입받아
     * JPAQueryFactory를 Bean으로 등록한다.
     */
    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }
}
