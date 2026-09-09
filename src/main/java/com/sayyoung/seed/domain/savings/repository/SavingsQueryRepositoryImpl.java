package com.sayyoung.seed.domain.savings.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sayyoung.seed.domain.savings.entity.Savings;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sayyoung.seed.domain.savings.entity.QSavings.savings;

/**
 * SavingsQueryRepository의 QueryDSL 구현체.
 */
@Repository
@RequiredArgsConstructor
public class SavingsQueryRepositoryImpl implements SavingsQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Savings> findAllByUserIdAndRootCategoryId(
            Long userId,
            String rootCategoryId
    ) {
        return queryFactory
                .selectFrom(savings)
                .where(
                        savings.user.id.eq(userId),
                        matchesRootCategory(rootCategoryId)
                )
                .fetch();
    }

    @Override
    public Page<Savings> findPageByUserIdAndRootCategoryId(
            Long userId,
            String rootCategoryId,
            Pageable pageable
    ) {
        List<Savings> content = queryFactory
                .selectFrom(savings)
                .where(
                        savings.user.id.eq(userId),
                        matchesRootCategory(rootCategoryId)
                )
                .orderBy(savings.savedAt.desc(), savings.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(savings.count())
                .from(savings)
                .where(
                        savings.user.id.eq(userId),
                        matchesRootCategory(rootCategoryId)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    /**
     * 저축 내역이 참조하는 카테고리 자신이 루트 카테고리이거나,
     * 그 부모가 루트 카테고리인 경우를 모두 매칭합니다.
     */
    private BooleanExpression matchesRootCategory(
            String rootCategoryId
    ) {
        return savings.category.id.eq(rootCategoryId)
                .or(savings.category.parent.id.eq(rootCategoryId));
    }
}
