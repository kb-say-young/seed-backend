package com.sayyoung.seed.domain.diagnosis.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sayyoung.seed.domain.diagnosis.dto.RoadmapItemDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Dify 워크플로우 실행 API(`/v1/workflows/run`) 응답 최상위 DTO입니다.
 * 실제 로드맵 항목은 {@code data.outputs.structured_output.roadmap_items}에 담겨 온다
 * (LLM 노드의 Structured Output 기능을 사용하는 워크플로우 기준).
 */
@Schema(description = "Dify 워크플로우 실행 응답")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DifyWorkflowResponseDto {

    private Data data;

    /**
     * roadmap_items에 바로 접근하기 위한 편의 메서드.
     * data/outputs/structured_output 중 무엇이 비어 있어도 null을 반환한다(호출부에서 검증한다).
     */
    public List<RoadmapItemDto> getRoadmapItems() {
        if (data == null || data.getOutputs() == null || data.getOutputs().getStructuredOutput() == null) {
            return null;
        }
        return data.getOutputs().getStructuredOutput().getRoadmapItems();
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Data {

        @Schema(description = "워크플로우 실행 상태", example = "succeeded")
        private String status;

        @Schema(description = "워크플로우 실행 실패 시 에러 메시지")
        private String error;

        private Outputs outputs;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Outputs {

        @JsonProperty("structured_output")
        private StructuredOutput structuredOutput;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class StructuredOutput {

        @JsonProperty("roadmap_items")
        private List<RoadmapItemDto> roadmapItems;
    }
}
