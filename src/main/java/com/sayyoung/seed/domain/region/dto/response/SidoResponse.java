package com.sayyoung.seed.domain.region.dto.response;

import com.sayyoung.seed.domain.region.entity.Region;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 시도 조회 결과를 반환하는 DTO입니다.
 */
@Schema(description = "시도 조회 응답")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SidoResponse {

    /**
     * 5자리 시도 코드입니다.
     */
    @Schema(description = "5자리 시도 코드", example = "11000")
    private final String regionCode;

    /**
     * 시도 이름입니다.
     */
    @Schema(description = "시도 이름", example = "서울특별시")
    private final String sidoName;

    /**
     * Region 엔티티를 시도 응답 DTO로 변환합니다.
     *
     * @param region 변환할 시도 엔티티
     * @return 변환된 시도 응답 DTO
     */
    public static SidoResponse from(
            Region region
    ) {
        return new SidoResponse(
                region.getRegionCode(),
                region.getSidoName()
        );
    }
}
