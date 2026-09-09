package com.sayyoung.seed.global.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sayyoung.seed.global.response.code.SuccessCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 페이지네이션 성공 API 응답의 공통 형식을 정의합니다.
 *
 * @param <T> 응답 데이터 타입
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonPropertyOrder({
        "success",
        "code",
        "message",
        "page",
        "size",
        "totalElements",
        "totalPages",
        "data"
})
public class PageApiResponse<T> {

    private final boolean success;
    private final String code;
    private final String message;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final List<T> data;

    /**
     * 페이지네이션 성공 응답을 생성합니다.
     */
    public static <T> PageApiResponse<T> success(
            SuccessCode successCode,
            Page<T> page
    ) {
        return new PageApiResponse<>(
                true,
                successCode.getCode(),
                successCode.getMessage(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getContent()
        );
    }
}