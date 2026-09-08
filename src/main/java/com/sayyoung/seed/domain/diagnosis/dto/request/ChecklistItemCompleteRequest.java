package com.sayyoung.seed.domain.diagnosis.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 체크리스트 항목 완료 처리 요청 DTO입니다.
 */
@Schema(description = "체크리스트 항목 완료 요청")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChecklistItemCompleteRequest {

    @Schema(description = "완료와 함께 기록할 비용(원)", example = "30000")
    private Long cost;

    @Schema(description = "비용 발생 일자", example = "2026-09-08")
    private LocalDate date;
}
