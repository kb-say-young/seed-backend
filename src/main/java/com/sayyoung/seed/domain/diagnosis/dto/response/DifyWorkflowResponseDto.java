package com.sayyoung.seed.domain.diagnosis.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sayyoung.seed.domain.diagnosis.dto.RoadmapItemDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Dify 로드맵 생성 워크플로우 응답 최상위 DTO입니다.
 */
@Schema(description = "Dify 로드맵 생성 워크플로우 응답")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DifyResponseDto {

    @Schema(description = "사용자 목표별 로드맵(추천) 항목 목록")
    @JsonProperty("roadmap_items")
    private List<RoadmapItemDto> roadmapItems;
}
