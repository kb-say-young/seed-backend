package com.sayyoung.seed.domain.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sayyoung.seed.domain.region.repository.RegionRepository;
import com.sayyoung.seed.domain.user.dto.response.DiagnosisInput;
import com.sayyoung.seed.domain.user.entity.UserGoal;
import com.sayyoung.seed.domain.user.exception.UserErrorCode;
import com.sayyoung.seed.domain.user.repository.UserGoalRepository;
import com.sayyoung.seed.domain.user.repository.UserRepository;
import com.sayyoung.seed.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiagnosisInputService {
    // 기존 UserService와 동일한 Jackson 2 사용. Spring Boot의 Jackson 3 빈과 분리한다.
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final UserRepository userRepository;
    private final UserGoalRepository userGoalRepository;
    private final RegionRepository regionRepository;

    /**
     * JWT 인증에서 얻은 users.id로 저장된 프로필과 목표를 조회한다.
     * 호출자가 인증/접근 권한을 확인해야 하며, 요청 파라미터의 임의 ID를 그대로 전달하지 않는다.
     * 존재하지 않는 회원은 USER_NOT_FOUND, 미입력 값은 null/빈 목표 목록으로 반환한다.
     */
    public DiagnosisInput findByUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException(com.sayyoung.seed.global.response.code.CommonErrorCode.UNAUTHORIZED);
        }
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        String regionDisplay = user.getRegionCode() == null ? null : regionRepository
                .findByRegionCode(user.getRegionCode())
                .map(region -> java.util.stream.Stream.of(region.getSidoName(), region.getSigunguName())
                        .filter(Objects::nonNull).filter(s -> !s.isBlank()).collect(Collectors.joining(" ")))
                .orElse(null);
        var profile = new DiagnosisInput.Profile(user.getBirthDate(), user.getProtectionEndDate(),
                user.getYouthSupport(), user.getRegionCode(), regionDisplay, user.getIncome(),
                user.getHouseholdSize(), user.getBasicRecipient(), user.getFixedBudget(),
                user.getBudget(), user.getHasCda());
        var goals = userGoalRepository.findDiagnosisGoalsByUserId(userId).stream().map(this::toGoal).toList();
        return new DiagnosisInput(user.getId(), profile, goals);
    }

    private DiagnosisInput.Goal toGoal(UserGoal goal) {
        var category = goal.getCategory();
        var parent = category.getParent();
        try {
            Map<String, Object> description = MAPPER.readValue(goal.getDescription(), new TypeReference<>() { });
            if (description == null) throw new IllegalArgumentException("description은 JSON 객체여야 합니다.");
            return new DiagnosisInput.Goal(goal.getId(), parent == null ? null : parent.getId(),
                    parent == null ? null : parent.getName(), category.getId(), category.getName(), description);
        } catch (JsonProcessingException | IllegalArgumentException e) {
            throw new IllegalStateException("저장된 목표 설명을 읽을 수 없습니다. goalId=" + goal.getId(), e);
        }
    }
}
