package com.sayyoung.seed.domain.policy.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sayyoung.seed.domain.policy.dto.PolicyMatchResult;
import com.sayyoung.seed.domain.policy.dto.PolicyFilterCondition;
import lombok.RequiredArgsConstructor;
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

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PolicyMatchResult> findByCondition(PolicyFilterCondition condition) {

        BooleanBuilder builder = new BooleanBuilder();

        // 카테고리 조건
        builder.and(policy.category.id.eq(condition.getCategoryId()));

        // 연령 조건
        if (condition.getAge() != null) {
            builder.and(
                    policy.minAge.eq(0)
                            .and(policy.maxAge.eq(0))
                            .or(policy.minAge.loe(condition.getAge())
                                    .and(policy.maxAge.goe(condition.getAge())))
            );
        }

        // 소득 조건
        if (condition.getIncome() != null) {
            builder.and(
                    policy.incomeMin.eq(0L)
                            .and(policy.incomeMax.eq(0L))
                            .or(policy.incomeMin.loe(condition.getIncome())
                                    .and(policy.incomeMax.goe(condition.getIncome())))
            );
        }

        // 신청 기간 조건
        if (condition.getCurrentDate() != null) {
            builder.and(
                    policy.applyStartDate.isNull()
                            .or(policy.applyStartDate.loe(condition.getCurrentDate()))
            );

            builder.and(
                    policy.applyEndDate.isNull()
                            .or(policy.applyEndDate.goe(condition.getCurrentDate()))
            );
        }

        JPQLQuery<PolicyMatchResult> query = queryFactory
                .selectDistinct(
                        Projections.constructor(
                                PolicyMatchResult.class,
                                policy.id,
                                policy.policyNo,
                                policy.name,
                                policy.description,
                                policy.institutionName
                        )
                )
                .from(policy);

        // 지역 조건이 있을 때만 조인
        if (condition.getRegionCode() != null) {
            query.leftJoin(policyRegion)
                    .on(policyRegion.policy.eq(policy));

            builder.and(
                    policyRegion.id.regionCode.eq(condition.getRegionCode())
            );
        }

        // 직업 조건이 있을 때만 조인
        if (condition.getJobCode() != null) {
            query.leftJoin(policyJob)
                    .on(policyJob.policy.eq(policy));

            builder.and(
                    policyJob.id.jobCode.eq("0013010")
                            .or(policyJob.id.jobCode.eq(condition.getJobCode()))
            );
        }

        // 학력 조건이 있을 때만 조인
        if (condition.getSchoolCode() != null) {
            query.leftJoin(policySchool)
                    .on(policySchool.policy.eq(policy));

            builder.and(
                    policySchool.id.schoolCode.eq("0049010")
                            .or(policySchool.id.schoolCode.eq(condition.getSchoolCode()))
            );
        }

        // 특화 대상 조건이 있을 때만 조인
        if (condition.getTargetCode() != null) {
            query.leftJoin(policyTarget)
                    .on(policyTarget.policy.eq(policy));

            builder.and(
                    policyTarget.id.targetCode.eq("0014010")
                            .or(policyTarget.id.targetCode.eq(condition.getTargetCode()))
            );
        }

        return query
                .where(builder)
                .fetch();
    }
}