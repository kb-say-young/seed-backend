package com.sayyoung.seed.domain.region.controller;

import com.sayyoung.seed.domain.region.dto.response.SidoResponse;
import com.sayyoung.seed.domain.region.dto.response.SigunguResponse;
import com.sayyoung.seed.domain.region.service.RegionService;
import com.sayyoung.seed.global.response.ApiResponse;
import com.sayyoung.seed.global.response.ResponseFactory;
import com.sayyoung.seed.global.response.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 시도 및 시군구 조회 API를 제공하는 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/regions")
public class RegionController implements RegionControllerDocs {

    private final RegionService regionService;

    /**
     * 전체 시도 목록을 조회합니다.
     *
     * @return 시도 목록
     */
    @GetMapping("/sidos")
    public ResponseEntity<ApiResponse<List<SidoResponse>>> getSidos() {
        List<SidoResponse> response = regionService.getSidos();

        return ResponseFactory
                .success(SuccessCode.COMMON_OK, response);
    }

    /**
     * 특정 시도에 포함된 시군구 목록을 조회합니다.
     *
     * @param sidoCode 조회할 시도의 지역 코드
     * @return 해당 시도의 시군구 목록
     */
    @GetMapping("/{sidoCode}/sigungus")
    public ResponseEntity<ApiResponse<List<SigunguResponse>>> getSigungus(
            @PathVariable String sidoCode
    ) {
        List<SigunguResponse> response = regionService.getSigungus(sidoCode);

        return ResponseFactory
                .success(SuccessCode.COMMON_OK, response);
    }
}
