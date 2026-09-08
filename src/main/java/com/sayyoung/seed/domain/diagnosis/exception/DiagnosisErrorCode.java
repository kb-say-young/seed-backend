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
    ),

    /**
     * 요청한 추천(로드맵) 항목이 존재하지 않는 경우 발생합니다.
     */
    RECOMMENDATION_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "DIAGNOSIS_404_004",
            "존재하지 않는 로드맵 항목입니다."
    ),

    /**
     * 요청한 체크리스트 항목이 존재하지 않는 경우 발생합니다.
     */
    CHECKLIST_ITEM_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "DIAGNOSIS_404_005",
            "존재하지 않는 체크리스트 항목입니다."
    ),

    /**
     * Dify 요청 데이터 직렬화에 실패한 경우 발생합니다.
     */
    DIFY_REQUEST_SERIALIZATION_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "DIAGNOSIS_500_001",
            "Dify 요청 데이터 변환에 실패했습니다."
    ),

    DIFY_RESPONSE_EMPTY(
            HttpStatus.BAD_GATEWAY,
            "DIAGNOSIS_502_001",
            "Dify 응답이 비어 있습니다."
    ),

    DIFY_API_CALL_FAILED(
            HttpStatus.BAD_GATEWAY,
            "DIAGNOSIS_502_002",
            "Dify API 호출에 실패했습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}