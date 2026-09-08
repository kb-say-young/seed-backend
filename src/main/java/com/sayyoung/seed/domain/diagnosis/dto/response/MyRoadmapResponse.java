package com.sayyoung.seed.domain.diagnosis.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 로드맵 화면(런웨이 바) 조회 응답 DTO입니다.
 */
@Schema(description = "내 로드맵 응답")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MyRoadmapResponse {

    @Schema(description = "보호종료(예정)일(yyyy.MM)", example = "2024.02")
    private final String protectionEndYm;

    @Schema(description = "계획 종료 시점(yyyy.MM)", example = "2029.02")
    private final String planUntilYm;

    @Schema(description = "로드맵 요약")
    private final RoadmapSummaryResponse summary;

    public static MyRoadmapResponse of(
            String protectionEndYm,
            String planUntilYm,
            RoadmapSummaryResponse summary
    ) {
        return new MyRoadmapResponse(
                protectionEndYm,
                planUntilYm,
                summary
        );
    }
}
