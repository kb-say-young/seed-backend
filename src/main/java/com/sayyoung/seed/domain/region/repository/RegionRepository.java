package com.sayyoung.seed.domain.region.repository;

import com.sayyoung.seed.domain.region.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 지역 정보 조회를 담당하는 리포지토리입니다.
 */
public interface RegionRepository extends JpaRepository<Region, Long> {

    /**
     * 상위 지역 코드가 없는 시도 목록을 지역 코드 순서로 조회합니다.
     *
     * @return 시도 목록
     */
    List<Region> findAllByParentCodeIsNullOrderByRegionCodeAsc();

    /**
     * 특정 시도에 포함된 시군구 목록을 지역 코드 순서로 조회합니다.
     *
     * @param parentCode 상위 시도 코드
     * @return 해당 시도에 포함된 시군구 목록
     */
    List<Region> findAllByParentCodeOrderByRegionCodeAsc(
            String parentCode
    );

    /**
     * 전달받은 지역 코드가 시도 코드로 존재하는지 확인합니다.
     *
     * @param regionCode 확인할 시도 코드
     * @return 시도 코드가 존재하면 true, 존재하지 않으면 false
     */
    boolean existsByRegionCodeAndParentCodeIsNull(
            String regionCode
    );
}
