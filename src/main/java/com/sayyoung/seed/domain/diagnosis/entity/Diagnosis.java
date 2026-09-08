package com.sayyoung.seed.domain.diagnosis.entity;

import com.sayyoung.seed.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 AI 진단 이력을 나타내는 엔티티입니다.
 */
@Entity
@Getter
@Table(name = "diagnosis")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diagnosis {

    private static final String STATUS_RUNNING = "running";
    private static final String STATUS_COMPLETED = "completed";
    private static final String STATUS_FAILED = "failed";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diagnosis_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    private Diagnosis(
            User user,
            String status
    ) {
        this.user = user;
        this.status = status;
    }

    /**
     * 진단을 시작 상태(running)로 생성합니다.
     */
    public static Diagnosis create(
            User user
    ) {
        return new Diagnosis(user, STATUS_RUNNING);
    }

    /**
     * 진단 결과 파싱·저장이 모두 성공했을 때 완료 상태로 전환합니다.
     */
    public void complete() {
        this.status = STATUS_COMPLETED;
    }

    /**
     * 진단 결과 파싱·저장 중 오류가 발생했을 때 실패 상태로 전환합니다.
     */
    public void fail() {
        this.status = STATUS_FAILED;
    }
}
