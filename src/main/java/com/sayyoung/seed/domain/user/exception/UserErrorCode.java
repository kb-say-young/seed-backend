package com.sayyoung.seed.domain.user.exception;

import com.sayyoung.seed.global.response.code.ErrorResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 사용자 도메인에서 발생하는 비즈니스 에러 코드를 정의합니다.
 */
@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorResponseCode {

    USER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "USER_404_001",
            "사용자를 찾을 수 없습니다."
    ),

    DUPLICATE_LOGIN_ID(
            HttpStatus.CONFLICT,
            "USER_409_001",
            "이미 사용 중인 아이디입니다."
    ),

    /**
     * region_code가 존재하지 않거나 시/도 레벨(시/군/구 레벨이 아닌) 코드인 경우 발생합니다.
     */
    INVALID_REGION_CODE(
            HttpStatus.BAD_REQUEST,
            "USER_400_001",
            "존재하지 않거나 시/군/구 단위가 아닌 지역 코드입니다."
    ),

    /**
     * category_id가 categories 테이블에 존재하지 않는 경우 발생합니다.
     */
    CATEGORY_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "USER_404_002",
            "존재하지 않는 목표 카테고리입니다."
    ),

    /**
     * category_id가 가리키는 카테고리의 실제 상위 카테고리가 요청의 parent_category_id와 다른 경우 발생합니다.
     */
    CATEGORY_PARENT_MISMATCH(
            HttpStatus.BAD_REQUEST,
            "USER_400_002",
            "category_id의 상위 카테고리가 parent_category_id와 일치하지 않습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
