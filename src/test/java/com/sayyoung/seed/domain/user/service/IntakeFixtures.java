package com.sayyoung.seed.domain.user.service;

import com.sayyoung.seed.domain.user.dto.request.GoalRequest;
import com.sayyoung.seed.domain.user.dto.request.IntakeRequest;
import com.sayyoung.seed.domain.user.dto.request.UserProfileRequest;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * {@link UserIntakePersistenceTest} 전용 임시 픽스처입니다.
 * IntakeRequest/UserProfileRequest/GoalRequest는 Jackson 역직렬화 전용(세터·빌더 없음)이라
 * 리플렉션으로 필드를 직접 채운다. 사용된 category_id("13": 공공임대/주거, "42": 적금/금융)는
 * V5 카테고리 시드, region_code("11110")는 V2 지역 시드에 존재하는 값이다.
 */
final class IntakeFixtures {

    private IntakeFixtures() {
    }

    static IntakeRequest request(String categoryId) {
        UserProfileRequest userProfile = newInstance(UserProfileRequest.class);
        set(userProfile, "protectionEndDate", LocalDate.of(2027, 3, 1));
        set(userProfile, "youthSupport", true);
        set(userProfile, "fixedBudget", 8_000_000L);
        set(userProfile, "regionCode", "11110");
        set(userProfile, "income", 1_500_000L);
        set(userProfile, "basicRecipient", false);
        set(userProfile, "householdSize", 1);
        set(userProfile, "education", "대학재학");

        GoalRequest goal = newInstance(GoalRequest.class);
        set(goal, "parentCategoryId", categoryId.substring(0, 1));
        set(goal, "categoryId", categoryId);
        set(goal, "description", Map.of("reason", "테스트 목표"));

        IntakeRequest request = newInstance(IntakeRequest.class);
        set(request, "userProfile", userProfile);
        set(request, "goals", List.of(goal));
        return request;
    }

    private static <T> T newInstance(Class<T> type) {
        try {
            var constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("테스트 픽스처 생성에 실패했습니다: " + type.getSimpleName(), e);
        }
    }

    private static void set(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("테스트 픽스처 필드 설정에 실패했습니다: " + fieldName, e);
        }
    }
}
