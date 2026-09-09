package com.sayyoung.seed.domain.savings.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

/**
 * 저축(savings) 화면에서 사용하는 카테고리(housing|work)와 categories 테이블의
 * 최상위(부모가 없는) 카테고리를 연결합니다.
 */
@Getter
@RequiredArgsConstructor
public enum SavingsCategory {

    HOUSING("housing", "1", "주거 지원"),
    WORK("work", "2", "취·창업 지원");

    private final String key;
    private final String rootCategoryId;
    private final String label;

    /**
     * 스펙의 category 값(대소문자 무관)에 해당하는 카테고리를 찾습니다.
     *
     * @param key 조회할 카테고리 키 (housing 또는 work)
     */
    public static Optional<SavingsCategory> fromKey(
            String key
    ) {
        return Arrays.stream(values())
                .filter(category -> category.key.equalsIgnoreCase(key))
                .findFirst();
    }
}
