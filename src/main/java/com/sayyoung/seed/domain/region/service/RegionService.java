package com.sayyoung.seed.domain.region.service;

import com.sayyoung.seed.domain.region.dto.response.SidoResponse;
import com.sayyoung.seed.domain.region.dto.response.SigunguResponse;
import com.sayyoung.seed.domain.region.entity.Region;
import com.sayyoung.seed.domain.region.exception.RegionErrorCode;
import com.sayyoung.seed.domain.region.repository.RegionRepository;
import com.sayyoung.seed.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 지역 조회와 관련된 비즈니스 로직을 처리하는 서비스입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegionService {

    private final RegionRepository regionRepository;

    /**
     * 전체 시도 목록을 조회합니다.
     *
     * @return 지역 코드 순으로 정렬된 시도 목록
     */
    public List<SidoResponse> getSidos() {
        return regionRepository
                .findAllByParentCodeIsNullOrderByRegionCodeAsc()
                .stream()
                .map(SidoResponse::from)
                .toList();
    }

    /**
     * 특정 시도에 포함된 시군구 목록을 조회합니다.
     *
     * @param sidoCode 조회할 시도의 5자리 지역 코드
     * @return 해당 시도에 포함된 시군구 목록
     * @throws IllegalArgumentException 존재하지 않는 시도 코드인 경우
     */
    public List<SigunguResponse> getSigungus(
            String sidoCode
    ) {
        // 전달받은 코드가 실제 시도 코드인지 확인
        validateSidoCode(sidoCode);

        // 시도 지역 코드가 부모인 시군구 지역 코드 조회
        return regionRepository
                .findAllByParentCodeOrderByRegionCodeAsc(sidoCode)
                .stream()
                .map(SigunguResponse::from)
                .toList();
    }

    /**
     * 시도 코드가 존재하는지 검증합니다.
     *
     * @param sidoCode 검증할 시도 코드
     * @throws IllegalArgumentException 존재하지 않는 시도 코드인 경우
     */
    private void validateSidoCode(
            String sidoCode
    ) {
        // 시도 지역 코드 존재여부 체크
        boolean isExists = regionRepository
                .existsByRegionCodeAndParentCodeIsNull(sidoCode);

        // 시도 지역 코드가 존재하지 않은 경우
        if (!isExists) {
            throw new BusinessException(RegionErrorCode.REGION_CODE_NOT_FOUND);
        }
    }

}
