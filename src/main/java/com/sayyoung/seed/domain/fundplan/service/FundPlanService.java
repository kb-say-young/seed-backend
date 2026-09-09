package com.sayyoung.seed.domain.fundplan.service;

import com.sayyoung.seed.domain.fundplan.dto.request.FundPlanAllocationUpdateRequest;
import com.sayyoung.seed.domain.fundplan.dto.response.FundPlanResponse;

public interface FundPlanService {

    /**
     * 예산(B1) 현황을 조회한다.
     *
     * @param userId 조회를 요청한 사용자 식별자
     */
    FundPlanResponse getFundPlan(
            Long userId
    );

    /**
     * 카테고리별 배분 비율을 수정한다(B1 '수정' 모드).
     *
     * @param userId  요청한 사용자 식별자
     * @param request 배분 목록(합계는 반드시 100)
     */
    FundPlanResponse updateAllocation(
            Long userId,
            FundPlanAllocationUpdateRequest request
    );
}
