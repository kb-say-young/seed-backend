package com.sayyoung.seed.domain.diagnosis.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 진단 생성(POST) 후 리다이렉트되는 단건 조회 응답 DTO입니다.
 */
@Schema(description = "진단 상태 응답")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DiagnosisStatusResponse {

    @Schema(description = "진단 ID", example = "1")
    private final Long diagnosisId;

    @Schema(description = "진단 상태 (running, completed, failed)", example = "completed")
    private final String status;

    public static DiagnosisStatusResponse of(
            Long diagnosisId,
            String status
    ) {
        return new DiagnosisStatusResponse(diagnosisId, status);
    }
}
