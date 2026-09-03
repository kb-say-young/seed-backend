package com.sayyoung.seed.domain.region.dto.response;

import com.sayyoung.seed.domain.region.entity.Region;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 시군구 조회 결과를 반환하는 DTO입니다.
 */
@Schema(description = "시군구 조회 응답")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SigunguResponse {

    /**
     * 5자리 시군구 코드입니다.
     */
    @Schema(description = "5자리 시군구 코드", example = "11110")
    private final String regionCode;

    /**
     * 시군구 이름입니다.
     */
    @Schema(description = "시군구 이름", example = "종로구")
    private final String sigunguName;

    /**
     * Region 엔티티를 시군구 응답 DTO로 변환합니다.
     *
     * @param region 변환할 시군구 엔티티
     * @return 변환된 시군구 응답 DTO
     */
    public static SigunguResponse from(
            Region region
    ) {
        return new SigunguResponse(
                region.getRegionCode(),
                region.getSigunguName()
        );
    }
}
