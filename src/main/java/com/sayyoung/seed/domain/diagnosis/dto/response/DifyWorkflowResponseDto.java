package com.sayyoung.seed.domain.diagnosis.dto.dify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Dify Workflow API 응답을 받기 위한 DTO.
 */
@Getter
@NoArgsConstructor
public class DifyWorkflowResponseDto {

    // 워크플로우 실행 결과 데이터
    private Data data;

    /**
     * Dify 응답의 data 영역.
     */
    @Getter
    @NoArgsConstructor
    public static class Data {

        private Outputs outputs;
    }

    /**
     * Dify END 노드의 출력값.
     */
    @Getter
    @NoArgsConstructor
    public static class Outputs {

        // AI가 생성한 로드맵 JSON 문자열
        @JsonProperty("roadmap_json")
        private String roadmapJson;
    }
}