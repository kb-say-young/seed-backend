package com.sayyoung.seed.global.response.code;

import org.springframework.http.HttpStatus;

/**
 * API 응답 코드가 공통으로 가져야 하는 규약을 정의합니다.
 *
 * <p>성공 및 실패 응답 코드는 HTTP 상태 코드,
 * 서비스 응답 코드, 기본 메시지를 제공합니다.</p>
 */
public interface ResponseCode {

    // HTTP 상태 코드 반환
    HttpStatus getHttpStatus();

    // 서비스 응답 코드 반환
    String getCode();

    // 기본 응답 메시지 반환
    String getMessage();
}
