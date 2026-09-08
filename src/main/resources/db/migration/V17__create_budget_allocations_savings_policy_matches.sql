-- V15__create_budget_allocations_savings_policy_matches.sql
-- 예산 배분/적립/정책 매칭 도메인 테이블 생성 (씨앗 1차.sql ERD 기준)
-- MySQL 8.x / Flyway
--
-- ERD의 recommandation.diagnosis_id / budget_allocations.latest_diagnosis_id는
-- VARCHAR(255)로 되어 있었으나, diagnosis.diagnosis_id가 BIGINT라 타입 불일치였다.
-- 이미 마이그레이션된 recommendations.diagnosis_id(BIGINT)와 동일하게 맞춘다.

CREATE TABLE budget_allocations (
    allocation_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '예산 배분 식별자',
    user_id BIGINT NOT NULL COMMENT '사용자 식별자',
    latest_diagnosis_id BIGINT NULL COMMENT 'ai_ratio/ai_amount를 갱신한 최신 진단',
    category_id VARCHAR(2) NOT NULL COMMENT '카테고리 ID',

    ai_ratio DECIMAL(5, 2) NOT NULL COMMENT 'AI 추천 배분 비율(%)',
    ai_amount DECIMAL(15, 0) NOT NULL COMMENT 'AI 추천 배분 금액',
    user_ratio DECIMAL(5, 2) NULL COMMENT '사용자가 슬라이더로 조정한 비율(%), 조정 전엔 NULL',
    user_amount DECIMAL(15, 0) NULL COMMENT '사용자가 조정한 배분 금액',

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',

    PRIMARY KEY (allocation_id),
    KEY idx_budget_allocations_user (user_id),
    KEY idx_budget_allocations_diagnosis (latest_diagnosis_id),
    KEY idx_budget_allocations_category (category_id),

    CONSTRAINT fk_budget_allocations_user
        FOREIGN KEY (user_id)
        REFERENCES users (user_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_budget_allocations_diagnosis
        FOREIGN KEY (latest_diagnosis_id)
        REFERENCES diagnosis (diagnosis_id)
        ON UPDATE CASCADE
        ON DELETE SET NULL,

    CONSTRAINT fk_budget_allocations_category
        FOREIGN KEY (category_id)
        REFERENCES categories (category_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='사용자 예산 카테고리별 배분(AI 추천 + 사용자 조정)';


CREATE TABLE savings (
    saving_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '적립 식별자',
    category_id VARCHAR(2) NOT NULL COMMENT '카테고리 ID',
    user_id BIGINT NOT NULL COMMENT '사용자 식별자',

    title VARCHAR(200) NOT NULL COMMENT '적립 항목 (예: 월세 적립)',
    amount DECIMAL(15, 0) NOT NULL COMMENT '적립 금액',
    saved_at DATE NOT NULL COMMENT '적립일',

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',

    PRIMARY KEY (saving_id),
    KEY idx_savings_user (user_id),
    KEY idx_savings_category (category_id),

    CONSTRAINT fk_savings_category
        FOREIGN KEY (category_id)
        REFERENCES categories (category_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_savings_user
        FOREIGN KEY (user_id)
        REFERENCES users (user_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='사용자 적립 내역';


CREATE TABLE policy_matches (
    policy_match_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '정책 매칭 식별자',
    policy_id BIGINT NOT NULL COMMENT '정책 ID',
    recommendations_id BIGINT NOT NULL COMMENT '연관된 추천(로드맵) 항목 ID',

    eligibility_status VARCHAR(20) NOT NULL COMMENT '자격 판정 상태',
    reason TEXT NULL COMMENT '판정 사유',

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',

    PRIMARY KEY (policy_match_id),
    KEY idx_policy_matches_policy (policy_id),
    KEY idx_policy_matches_recommendation (recommendations_id),

    CONSTRAINT fk_policy_matches_policy
        FOREIGN KEY (policy_id)
        REFERENCES policies (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_policy_matches_recommendation
        FOREIGN KEY (recommendations_id)
        REFERENCES recommendations (recommendations_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='정책별 자격 판정(매칭) 결과';
