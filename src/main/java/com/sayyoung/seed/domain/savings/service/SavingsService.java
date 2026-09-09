package com.sayyoung.seed.domain.savings.service;

import com.sayyoung.seed.domain.savings.dto.request.SavingsCreateRequest;
import com.sayyoung.seed.domain.savings.dto.response.SavingsCreateResponse;
import com.sayyoung.seed.domain.savings.dto.response.SavingsDetailResponse;
import com.sayyoung.seed.domain.savings.dto.response.SavingsSummaryResponse;
import org.springframework.data.domain.Pageable;

public interface SavingsService {

    /**
     * 모은 돈(A1) 요약을 조회한다.
     *
     * @param userId 조회를 요청한 사용자 식별자
     */
    SavingsSummaryResponse getSavings(
            Long userId
    );

    /**
     * 카테고리별(A2) 저축 상세를 조회한다.
     *
     * @param userId     조회를 요청한 사용자 식별자
     * @param categoryKey housing 또는 work
     * @param pageable   저축 내역(records) 페이지네이션 정보
     */
    SavingsDetailResponse getSavingsDetail(
            Long userId,
            String categoryKey,
            Pageable pageable
    );

    /**
     * 저축 내역을 등록(S3)한다.
     *
     * @param userId  등록을 요청한 사용자 식별자
     * @param request 등록할 저축 내역 정보
     */
    SavingsCreateResponse createSaving(
            Long userId,
            SavingsCreateRequest request
    );
}
