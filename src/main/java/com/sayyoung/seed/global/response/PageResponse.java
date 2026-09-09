package com.sayyoung.seed.global.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 페이지네이션 조회 결과의 공통 응답 형식을 정의합니다.
 *
 * @param <T> 응답 데이터 타입
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PageResponse<T> {

    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final List<T> items;

    /**
     * Spring Data의 Page 객체를 공통 페이지 응답으로 변환합니다.
     */
    public static <T> PageResponse<T> from(
            Page<T> page
    ) {
        return new PageResponse<>(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getContent()
        );
    }
}
