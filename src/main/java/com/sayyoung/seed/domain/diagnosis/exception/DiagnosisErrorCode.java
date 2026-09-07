package com.sayyoung.seed.domain.diagnosis.exception;

import com.sayyoung.seed.global.response.code.ErrorResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 진단 도메인에서 발생하는 비즈니스 에러 코드를 정의합니다.
 */
@Getter
@RequiredArgsConstructor
public enum DiagnosisErrorCode implements ErrorResponseCode {

    DIAGNOSIS_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "DIAGNOSIS_404_001",
            "진단 정보를 찾을 수 없습니다."
    ),

    /**
     * LLM(Dify) 응답 JSON이 기대한 스키마와 다르거나 파싱에 실패한 경우 발생합니다.
     */
    INVALID_LLM_RESPONSE(
            HttpStatus.BAD_REQUEST,
            "DIAGNOSIS_400_001",
            "LLM 응답 형식이 올바르지 않습니다."
    ),

    /**
     * roadmap_item의 origin_sub_category와 일치하는 카테고리를 찾지 못한 경우 발생합니다.
     */
    CATEGORY_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "DIAGNOSIS_404_002",
            "일치하는 카테고리를 찾을 수 없습니다."
    ),

    /**
     * 진단 대상 사용자가 해당 카테고리로 설정한 목표를 찾지 못한 경우 발생합니다.
     */
    GOAL_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "DIAGNOSIS_404_003",
            "일치하는 사용자 목표를 찾을 수 없습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
