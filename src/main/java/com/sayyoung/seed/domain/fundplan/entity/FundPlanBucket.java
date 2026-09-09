package com.sayyoung.seed.domain.fundplan.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

/**
 * 예산(fund-plan) 화면에서 사용하는 4개 버킷(housing|living|work|saving)과 categories 테이블의
 * 최상위(부모가 없는) 카테고리를 연결합니다.
 */
@Getter
@RequiredArgsConstructor
public enum FundPlanBucket {

    HOUSING("housing", "1", "주거 지원"),
    LIVING("living", "3", "생활 지원"),
    WORK("work", "2", "취·창업 지원"),
    SAVING("saving", "4", "금융 지원");

    private final String key;
    private final String rootCategoryId;
    private final String label;

    /**
     * 스펙의 key 값(대소문자 무관)에 해당하는 버킷을 찾습니다.
     *
     * @param key 조회할 버킷 키 (housing, living, work, saving)
     */
    public static Optional<FundPlanBucket> fromKey(
            String key
    ) {
        return Arrays.stream(values())
                .filter(bucket -> bucket.key.equalsIgnoreCase(key))
                .findFirst();
    }
}
