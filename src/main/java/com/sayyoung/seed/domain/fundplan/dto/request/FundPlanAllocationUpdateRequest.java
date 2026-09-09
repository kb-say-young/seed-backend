package com.sayyoung.seed.domain.fundplan.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 예산 배분 수정(B1 '수정' 모드) 요청 DTO입니다.
 */
@Schema(description = "예산 배분 수정 요청")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FundPlanAllocationUpdateRequest {

    @Schema(description = "배분 목록 (합계는 반드시 100)")
    @NotEmpty(message = "배분 목록은 필수입니다.")
    @Valid
    private List<FundPlanAllocationRequest> allocations;
}
