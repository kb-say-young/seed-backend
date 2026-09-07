package com.sayyoung.seed.global.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sayyoung.seed.domain.diagnosis.dto.DifyRoadmapResponse;

/**
 * Dify 워크플로우가 반환한 로드맵 응답 JSON을 파싱합니다.
 * 트리거(호출부)는 별도로 구현되며, 이 클래스는 파싱만 담당한다.
 */
public final class DifyResponseParser {

    // Spring Boot 4.1의 자동 구성 ObjectMapper 빈은 Jackson 3(tools.jackson.databind) 타입이라
    // 여기서 필요한 Jackson 2(com.fasterxml.jackson.databind) 타입 빈이 없다. 파싱 용도로만
    // 쓰이므로 빈 주입 없이 직접 생성한다 (UserService와 동일한 이유).
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private DifyResponseParser() {
    }

    /**
     * Dify 로드맵 응답 JSON 문자열을 파싱합니다.
     *
     * @param rawJson Dify 워크플로우가 반환한 원본 JSON 문자열
     * @return 파싱된 로드맵 응답
     * @throws DifyResponseParseException JSON 형식이 올바르지 않거나 스키마와 다른 경우
     */
    public static DifyRoadmapResponse parse(String rawJson) {
        try {
            return OBJECT_MAPPER.readValue(rawJson, DifyRoadmapResponse.class);
        } catch (JsonProcessingException e) {
            throw new DifyResponseParseException("Dify 응답 JSON 파싱에 실패했습니다.", e);
        }
    }
}
