package com.sayyoung.seed.domain.user.entity;

import com.sayyoung.seed.domain.policy.entity.Category;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 진단(목표 선택) 단계에서 제출된 사용자 목표입니다.
 * 세부 목표별 추가 질문(description)은 category마다 스키마가 달라 JSON으로 저장한다
 * (`카테고리별_요청_API_계약서.md` §4).
 */
@Entity
@Getter
@Table(name = "user_goals")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "description", nullable = false, columnDefinition = "json")
    private String description;

    private UserGoal(
            User user,
            Category category,
            String description
    ) {
        this.user = user;
        this.category = category;
        this.description = description;
    }

    /**
     * 사용자 목표를 생성합니다.
     *
     * @param description 세부 목표별 추가 질문 응답을 직렬화한 JSON 문자열
     */
    public static UserGoal create(
            User user,
            Category category,
            String description
    ) {
        return new UserGoal(user, category, description);
    }
}
