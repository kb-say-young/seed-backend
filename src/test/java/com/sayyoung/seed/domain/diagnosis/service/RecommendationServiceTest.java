package com.sayyoung.seed.domain.diagnosis.service;

import com.sayyoung.seed.domain.diagnosis.dto.request.RecommendationCategory;
import com.sayyoung.seed.domain.diagnosis.dto.response.RecommendationDetailResponse;
import com.sayyoung.seed.domain.diagnosis.dto.response.RecommendationResponse;
import com.sayyoung.seed.domain.diagnosis.entity.Diagnosis;
import com.sayyoung.seed.domain.diagnosis.exception.DiagnosisErrorCode;
import com.sayyoung.seed.domain.diagnosis.repository.DiagnosisRepository;
import com.sayyoung.seed.domain.user.entity.User;
import com.sayyoung.seed.domain.user.repository.UserRepository;
import com.sayyoung.seed.global.exception.BusinessException;
import com.sayyoung.seed.global.response.code.CommonErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * V14 마이그레이션이 적재하는 로컬·개발 테스트용 샘플 데이터(진단 1 - 월세/적금 추천 2건, 진단 2 - 교육 지원금 추천 1건)를
 * 기준으로 검증한다.
 */
@SpringBootTest
class RecommendationServiceTest {

    private static final Long SEEDED_USER_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;
    private static final Long SEEDED_DIAGNOSIS_ID = 1L;
    private static final Long SEEDED_RECOMMENDATION_ID = 1L;
    private static final Long NOT_EXISTING_DIAGNOSIS_ID = 999_999L;
    private static final Long NOT_EXISTING_RECOMMENDATION_ID = 999_999L;

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private DiagnosisRepository diagnosisRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 카테고리_필터가_없으면_해당_진단의_전체_추천_목록을_조회한다() {

        // when
        List<RecommendationResponse> responses = recommendationService.getRecommendations(SEEDED_DIAGNOSIS_ID, null);

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses)
                .extracting(RecommendationResponse::getTitle)
                .containsExactlyInAnyOrder("월세 적립 계획 수립", "청년 적금 우대 정책 활용");
    }

    @Test
    void 카테고리를_지정하면_해당_상위_카테고리의_추천만_조회한다() {

        // when
        List<RecommendationResponse> responses = recommendationService.getRecommendations(
                SEEDED_DIAGNOSIS_ID,
                RecommendationCategory.HOUSING
        );

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getTitle()).isEqualTo("월세 적립 계획 수립");
        assertThat(responses.get(0).getCategory()).isEqualTo("월세");
    }

    @Test
    void 연관된_추천이_없으면_빈_목록을_반환한다() {

        // given
        User user = userRepository.findById(SEEDED_USER_ID).orElseThrow();
        Diagnosis diagnosis = diagnosisRepository.save(Diagnosis.create(user));

        try {
            // when
            List<RecommendationResponse> responses = recommendationService.getRecommendations(diagnosis.getId(), null);

            // then
            assertThat(responses).isEmpty();
        } finally {
            diagnosisRepository.deleteById(diagnosis.getId());
        }
    }

    @Test
    void 존재하지_않는_진단이면_예외를_던진다() {

        // when & then
        assertThatThrownBy(() -> recommendationService.getRecommendations(NOT_EXISTING_DIAGNOSIS_ID, null))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(DiagnosisErrorCode.DIAGNOSIS_NOT_FOUND);
    }

    @Test
    void 추천_항목_식별자로_상세를_조회한다() {

        // when
        RecommendationDetailResponse response = recommendationService.getRecommendationDetail(
                SEEDED_USER_ID,
                SEEDED_RECOMMENDATION_ID
        );

        // then
        assertThat(response.getRecommendationId()).isEqualTo(SEEDED_RECOMMENDATION_ID);
        assertThat(response.getTitle()).isEqualTo("월세 적립 계획 수립");
        assertThat(response.getNextAction()).isEqualTo("적립 전용 계좌를 개설하고 자동이체를 등록하세요.");

        assertThat(response.getChecklistItems()).hasSize(2);
        assertThat(response.getChecklistItems())
                .extracting("status")
                .containsExactly("done", "todo");
    }

    @Test
    void 존재하지_않는_추천_항목이면_예외를_던진다() {

        // when & then
        assertThatThrownBy(() -> recommendationService.getRecommendationDetail(
                SEEDED_USER_ID,
                NOT_EXISTING_RECOMMENDATION_ID
        ))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(DiagnosisErrorCode.RECOMMENDATION_NOT_FOUND);
    }

    @Test
    void 다른_사용자의_추천_항목을_조회하면_예외를_던진다() {

        // when & then
        assertThatThrownBy(() -> recommendationService.getRecommendationDetail(
                OTHER_USER_ID,
                SEEDED_RECOMMENDATION_ID
        ))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(CommonErrorCode.FORBIDDEN);
    }
}
