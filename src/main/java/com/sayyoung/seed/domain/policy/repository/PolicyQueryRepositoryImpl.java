package com.sayyoung.seed.domain.policy.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sayyoung.seed.domain.policy.dto.PolicyFilterCondition;
import com.sayyoung.seed.domain.policy.dto.PolicyMatchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sayyoung.seed.domain.policy.entity.QPolicy.policy;
import static com.sayyoung.seed.domain.policy.entity.QPolicyJob.policyJob;
import static com.sayyoung.seed.domain.policy.entity.QPolicyRegion.policyRegion;
import static com.sayyoung.seed.domain.policy.entity.QPolicySchool.policySchool;
import static com.sayyoung.seed.domain.policy.entity.QPolicyTarget.policyTarget;

/**
 * PolicyQueryRepository의 QueryDSL 구현체.
 * 필요한 조건과 조인만 동적으로 추가한다.
 */
@Repository
@RequiredArgsConstructor
public class PolicyQueryRepositoryImpl implements PolicyQueryRepository {

    // 직업 제한 없음 코드
    private static final String JOB_UNRESTRICTED_CODE = "0013010";

    // 학력 제한 없음 코드
    private static final String SCHOOL_UNRESTRICTED_CODE = "0049010";

    // 특화 대상 제한 없음 코드
    private static final String TARGET_UNRESTRICTED_CODE = "0014010";

    // QueryDSL 쿼리 생성 객체
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PolicyMatchResult> findMatchedPolicies(
            PolicyFilterCondition condition,
            Pageable pageable
    ) {
        // 정책 필터 조건 생성
        BooleanBuilder builder = createCondition(condition);

        // 정책 목록 조회 쿼리 생성
        JPQLQuery<PolicyMatchResult> contentQuery = queryFactory
                .selectDistinct(
                        Projections.constructor(
                                PolicyMatchResult.class,
                                policy.id,
                                policy.name,
                                policy.description,
                                policy.institutionName,
                                policy.applyStartDate,
                                policy.applyEndDate,
                                policy.independentYouth
                        )
                )
                .from(policy);

        // 필요한 연관 테이블만 조인
        applyJoins(
                contentQuery,
                condition
        );

        // 페이지 단위 정책 조회
        List<PolicyMatchResult> content = contentQuery
                .where(builder)
                .orderBy(policy.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 조회 쿼리 생성
        JPQLQuery<Long> countQuery = queryFactory
                .select(policy.id.countDistinct())
                .from(policy);

        // 목록 조회와 동일한 조인 적용
        applyJoins(
                countQuery,
                condition
        );

        Long total = countQuery
                .where(builder)
                .fetchOne();

        return new PageImpl<>(
                content,
                pageable,
                total == null ? 0L : total
        );
    }

    /**
     * 사용자 정보에 따른 정책 필터 조건을 생성한다.
     */
    private BooleanBuilder createCondition(
            PolicyFilterCondition condition
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        // 카테고리 조건
        builder.and(
                policy.category.id.eq(
                        condition.getCategoryId()
                )
        );

        // 연령 조건
        if (condition.getAge() != null) {
            builder.and(
                    policy.minAge.eq(0)
                            .and(policy.maxAge.eq(0))
                            .or(
                                    policy.minAge.loe(
                                            condition.getAge()
                                    ).and(
                                            policy.maxAge.goe(
                                                    condition.getAge()
                                            )
                                    )
                            )
            );
        }

        // 소득 조건
        if (condition.getIncome() != null) {
            builder.and(
                    policy.incomeMin.eq(0L)
                            .and(policy.incomeMax.eq(0L))
                            .or(
                                    policy.incomeMin.loe(
                                            condition.getIncome()
                                    ).and(
                                            policy.incomeMax.goe(
                                                    condition.getIncome()
                                            )
                                    )
                            )
            );
        }

        // 신청 기간 조건
        if (condition.getCurrentDate() != null) {
            builder.and(
                    policy.applyStartDate.isNull()
                            .or(
                                    policy.applyStartDate.loe(
                                            condition.getCurrentDate()
                                    )
                            )
            );

            builder.and(
                    policy.applyEndDate.isNull()
                            .or(
                                    policy.applyEndDate.goe(
                                            condition.getCurrentDate()
                                    )
                            )
            );
        }

        // 지역 조건
        if (condition.getRegionCode() != null) {
            builder.and(
                    policyRegion.id.regionCode.eq(
                            condition.getRegionCode()
                    )
            );
        }

        // 직업 조건
        if (condition.getJobCode() != null) {
            builder.and(
                    policyJob.id.jobCode.eq(
                            JOB_UNRESTRICTED_CODE
                    ).or(
                            policyJob.id.jobCode.eq(
                                    condition.getJobCode()
                            )
                    )
            );
        }

        // 학력 조건
        if (condition.getSchoolCode() != null) {
            builder.and(
                    policySchool.id.schoolCode.eq(
                            SCHOOL_UNRESTRICTED_CODE
                    ).or(
                            policySchool.id.schoolCode.eq(
                                    condition.getSchoolCode()
                            )
                    )
            );
        }

        // 특화 대상 조건
        if (condition.getTargetCode() != null) {
            builder.and(
                    policyTarget.id.targetCode.eq(
                            TARGET_UNRESTRICTED_CODE
                    ).or(
                            policyTarget.id.targetCode.eq(
                                    condition.getTargetCode()
                            )
                    )
            );
        }

        return builder;
    }

    /**
     * 필요한 조건에 따라 연관 테이블을 동적으로 조인한다.
     */
    private void applyJoins(
            JPQLQuery<?> query,
            PolicyFilterCondition condition
    ) {
        // 지역 조건
        if (condition.getRegionCode() != null) {
            query.leftJoin(policyRegion)
                    .on(policyRegion.policy.eq(policy));
        }

        // 직업 조건
        if (condition.getJobCode() != null) {
            query.leftJoin(policyJob)
                    .on(policyJob.policy.eq(policy));
        }

        // 학력 조건
        if (condition.getSchoolCode() != null) {
            query.leftJoin(policySchool)
                    .on(policySchool.policy.eq(policy));
        }

        // 특화 대상 조건
        if (condition.getTargetCode() != null) {
            query.leftJoin(policyTarget)
                    .on(policyTarget.policy.eq(policy));
        }
    }
}