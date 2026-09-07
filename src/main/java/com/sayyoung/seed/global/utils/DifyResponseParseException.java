package com.sayyoung.seed.global.utils;

/**
 * Dify 응답 JSON이 기대한 스키마와 다르거나 파싱에 실패했을 때 발생합니다.
 */
public class DifyResponseParseException extends RuntimeException {

    public DifyResponseParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
