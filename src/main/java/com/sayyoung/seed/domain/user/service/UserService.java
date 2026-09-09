package com.sayyoung.seed.domain.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sayyoung.seed.domain.auth.dto.response.TokenResponse;
import com.sayyoung.seed.domain.auth.jwt.JwtProvider;
import com.sayyoung.seed.domain.auth.service.RefreshTokenService;
import com.sayyoung.seed.domain.policy.entity.Category;
import com.sayyoung.seed.domain.policy.repository.CategoryRepository;
import com.sayyoung.seed.domain.region.entity.Region;
import com.sayyoung.seed.domain.region.exception.RegionErrorCode;
import com.sayyoung.seed.domain.region.repository.RegionRepository;
import com.sayyoung.seed.domain.user.dto.request.GoalRequest;
import com.sayyoung.seed.domain.user.dto.request.IntakeRequest;
import com.sayyoung.seed.domain.user.dto.request.LoginRequest;
import com.sayyoung.seed.domain.user.dto.request.SignUpRequest;
import com.sayyoung.seed.domain.user.dto.request.UserProfileRequest;
import com.sayyoung.seed.domain.user.dto.response.UserResponse;
import com.sayyoung.seed.domain.user.entity.User;
import com.sayyoung.seed.domain.user.entity.UserGoal;
import com.sayyoung.seed.domain.user.exception.UserErrorCode;
import com.sayyoung.seed.domain.user.repository.UserGoalRepository;
import com.sayyoung.seed.domain.user.repository.UserRepository;
import com.sayyoung.seed.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 사용자 관련 비즈니스 로직을 처리합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    // Spring Boot 4.1의 자동 구성 ObjectMapper 빈은 Jackson 3(tools.jackson.databind) 타입이라
    // 여기서 필요한 Jackson 2(com.fasterxml.jackson.databind) 타입 빈이 없다. 목표 설명(JSON 직렬화)
    // 용도로만 쓰이므로 빈 주입 없이 직접 생성한다.
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final UserRepository userRepository;
    private final UserGoalRepository userGoalRepository;
    private final CategoryRepository categoryRepository;
    private final RegionRepository regionRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    /**
     * 로그인 아이디만으로 회원가입을 처리하는 스텁 메서드입니다.
     *
     * @param request 회원가입 요청
     * @return 생성된 사용자 정보
     * @throws BusinessException 이미 사용 중인 아이디인 경우
     */
    @Transactional
    public UserResponse signUp(
            SignUpRequest request
    ) {
        validateDuplicateLoginId(request.getLoginId());

        User user = User.create(
                request.getLoginId(),
                request.getName(),
                request.getBirthDate(),
                request.getPhoneNumber()
        );
        userRepository.save(user);

        return UserResponse.from(user);
    }

    /**
     * 로그인 아이디만으로 로그인을 처리하고 액세스/리프레시 토큰을 발급합니다.
     *
     * @param request 로그인 요청
     * @return 발급된 액세스/리프레시 토큰
     * @throws BusinessException 아이디에 해당하는 사용자가 없는 경우
     */
    public TokenResponse login(
            LoginRequest request
    ) {
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        String accessToken = jwtProvider.createAccessToken(user.getId());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());
        refreshTokenService.save(user.getId(), refreshToken);

        return TokenResponse.of(accessToken, refreshToken);
    }

    /**
     * 로그인 이후 화면에서 입력받은 진단 공통 프로필과 목표를 저장합니다.
     * `카테고리별_요청_API_계약서.md` §2~§3 규칙(지역 코드는 시/군/구 레벨만 허용,
     * category_id의 상위 카테고리는 parent_category_id와 일치해야 함)을 검증한다.
     * 재제출 시 프로필은 갱신되고, 목표 목록은 전체 교체된다.
     *
     * @param userId  인증된 사용자 식별자
     * @param request 진단 정보 제출 요청
     * @throws BusinessException 사용자/카테고리/지역 코드가 유효하지 않은 경우
     */
    @Transactional
    public void submitIntake(
            Long userId,
            IntakeRequest request
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        UserProfileRequest profile = request.getUserProfile();
        validateSigunguRegionCode(profile.getRegionCode());

        user.updateProfile(
                profile.getProtectionEndDate(),
                profile.getYouthSupport(),
                BigDecimal.valueOf(profile.getFixedBudget()),
                profile.getRegionCode(),
                profile.getIncome(),
                profile.getBasicRecipient(),
                profile.getHouseholdSize().shortValue(),
                profile.getEducation()
        );

        userGoalRepository.deleteAllByUserId(userId);
        request.getGoals().stream()
                .map(goalRequest -> toUserGoal(user, goalRequest))
                .forEach(userGoalRepository::save);
    }

    private UserGoal toUserGoal(
            User user,
            GoalRequest goalRequest
    ) {
        Category category = validateCategory(goalRequest.getParentCategoryId(), goalRequest.getCategoryId());

        return UserGoal.create(
                user,
                category,
                writeDescriptionAsJson(goalRequest.getDescription())
        );
    }

    /**
     * category_id가 categories 테이블에 존재하고, 그 상위 카테고리가 parent_category_id와
     * 일치하는지 검증한다(계약서 §2 규칙5).
     */
    private Category validateCategory(
            String parentCategoryId,
            String categoryId
    ) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.CATEGORY_NOT_FOUND));

        String actualParentCategoryId = category.getParent() == null ? null : category.getParent().getId();
        if (!Objects.equals(parentCategoryId, actualParentCategoryId)) {
            throw new BusinessException(UserErrorCode.CATEGORY_PARENT_MISMATCH);
        }

        return category;
    }

    /**
     * region_code가 존재하고 시/군/구 레벨(상위 코드를 가짐)인지 검증한다(계약서 §3.1).
     */
    private void validateSigunguRegionCode(
            String regionCode
    ) {
        Region region = regionRepository.findByRegionCode(regionCode)
                .orElseThrow(() -> new BusinessException(RegionErrorCode.REGION_CODE_NOT_FOUND));

        if (region.getParentCode() == null) {
            throw new BusinessException(UserErrorCode.INVALID_REGION_CODE);
        }
    }

    private String writeDescriptionAsJson(
            Object description
    ) {
        try {
            return OBJECT_MAPPER.writeValueAsString(description);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("목표 설명 직렬화에 실패했습니다.", e);
        }
    }

    private void validateDuplicateLoginId(
            String loginId
    ) {
        if (userRepository.existsByLoginId(loginId)) {
            throw new BusinessException(UserErrorCode.DUPLICATE_LOGIN_ID);
        }
    }
}
