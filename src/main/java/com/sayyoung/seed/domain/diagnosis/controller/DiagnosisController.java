package com.sayyoung.seed.domain.diagnosis.controller;

import com.sayyoung.seed.domain.diagnosis.dto.request.DiagnosisRequestDto;
import com.sayyoung.seed.domain.diagnosis.dto.response.DiagnosisSummaryResponse;
import com.sayyoung.seed.domain.diagnosis.service.DiagnosisService;
import com.sayyoung.seed.domain.diagnosis.service.DiagnosisSummaryService;
import com.sayyoung.seed.global.response.ApiResponse;
import com.sayyoung.seed.global.response.ResponseFactory;
import com.sayyoung.seed.global.response.code.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * 사용자 진단 요청을 처리하는 컨트롤러.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diagnoses")
public class DiagnosisController {

    private final DiagnosisService diagnosisService;
    private final DiagnosisSummaryService diagnosisSummaryService;

    /**
     * AI 진단을 수행하고 결과 조회 URI로 리다이렉트한다.
     */
    @PostMapping
    public ResponseEntity<Void> diagnose(
            @RequestParam Long userId,
            @Valid @RequestBody DiagnosisRequestDto requestDto
    ) {
        // 진단 수행 후 저장된 진단 ID 반환
        Long diagnosisId = diagnosisService.diagnose(
                userId,
                requestDto
        );

        // PRG 패턴을 위해 결과 조회 URI 생성
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{diagnosisId}")
                .buildAndExpand(diagnosisId)
                .toUri();

        // POST 처리 완료 후 GET 결과 조회 URI로 이동
        return ResponseEntity
                .status(HttpStatus.SEE_OTHER)
                .location(location)
                .build();
    }

//    /**
//     * 저장된 진단 결과를 조회한다.
//     */
//    @GetMapping("/{diagnosisId}")
//    public ResponseEntity getDiagnosis(
//            @PathVariable Long diagnosisId
//    ) {
//
//    }

    /**
     * 로드맵 화면 상단에 노출할 진단 요약 정보를 조회한다.
     */
    @GetMapping("/{diagnosisId}/summary")
    public ResponseEntity<ApiResponse<DiagnosisSummaryResponse>> getSummary(
            @PathVariable Long diagnosisId
    ) {
        DiagnosisSummaryResponse response = diagnosisSummaryService.getSummary(diagnosisId);

        return ResponseFactory
                .success(SuccessCode.COMMON_OK, response);
    }
}
