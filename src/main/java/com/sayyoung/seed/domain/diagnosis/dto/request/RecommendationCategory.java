package com.sayyoung.seed.domain.diagnosis.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 로드맵(추천) 목록 조회 시 화면 상단 탭에서 사용하는 상위 카테고리 필터입니다.
 * 각 값은 categories 테이블의 최상위(부모가 없는) 카테고리명에 대응합니다.
 */
@Getter
@RequiredArgsConstructor
public enum RecommendationCategory {

    HOUSING("주거 지원"),
    LIVING("생활 지원"),
    JOB_STARTUP("취·창업 지원"),
    FINANCE("금융 지원");

    private final String topLevelCategoryName;
}
