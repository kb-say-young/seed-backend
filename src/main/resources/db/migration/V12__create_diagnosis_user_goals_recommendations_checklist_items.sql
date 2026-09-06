-- 진단/목표/로드맵(추천)/체크리스트 도메인 테이블 생성
-- MySQL 8.x / Flyway
--
-- 생성 순서: diagnosis -> user_goals -> recommendations -> checklist_items (FK 의존 순서)

CREATE TABLE diagnosis (
    diagnosis_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '진단 식별자',
    user_id BIGINT NOT NULL COMMENT '진단 대상 사용자 ID',
    status VARCHAR(20) NOT NULL COMMENT '진단 상태: running, completed, failed, stale',
    summary TEXT NULL COMMENT '진단 결과 요약',

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',

    PRIMARY KEY (diagnosis_id),
    KEY idx_diagnosis_user (user_id),

    CONSTRAINT fk_diagnosis_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT ck_diagnosis_status
        CHECK (status IN ('running', 'completed', 'failed', 'stale'))
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='사용자 AI 진단 이력';


CREATE TABLE user_goals (
    goal_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '사용자 목표 식별자',
    category_id VARCHAR(2) NOT NULL COMMENT '선택한 세부목표 카테고리 ID',
    user_id BIGINT NOT NULL COMMENT '목표를 설정한 사용자 ID',
    description JSON NOT NULL COMMENT '목표 상세 설명(구조화 데이터)',
    flow_type VARCHAR(20) NOT NULL COMMENT '진행 방식: process, policy_only, mixed',
    status VARCHAR(20) NOT NULL COMMENT '목표 상태: active, done, cancelled',
    target_amount DECIMAL(15, 0) NULL COMMENT '목표 금액',
    target_date DATE NULL COMMENT '목표 달성 예정일',
    priority_rank SMALLINT NOT NULL COMMENT '사용자 목표 우선순위',

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',

    PRIMARY KEY (goal_id),
    KEY idx_user_goals_user (user_id),
    KEY idx_user_goals_category (category_id),

    CONSTRAINT fk_user_goals_category
        FOREIGN KEY (category_id)
        REFERENCES categories (category_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_user_goals_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT ck_user_goals_flow_type
        CHECK (flow_type IN ('process', 'policy_only', 'mixed')),

    CONSTRAINT ck_user_goals_status
        CHECK (status IN ('active', 'done', 'cancelled'))
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='사용자가 설정한 목표';


CREATE TABLE recommendations (
    recommendations_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '추천(로드맵) 항목 식별자',
    category_id VARCHAR(2) NOT NULL COMMENT '카테고리 ID',
    goal_id BIGINT NOT NULL COMMENT '연관된 사용자 목표 ID',
    diagnosis_id BIGINT NOT NULL COMMENT '추천을 생성한 진단 ID',
    item_key VARCHAR(100) NOT NULL COMMENT '추천 항목 키',
    order_no INT NOT NULL COMMENT '표시 순서',
    start_offset_value INT NOT NULL COMMENT '시작 시점 오프셋 값',
    start_offset_unit VARCHAR(10) NOT NULL COMMENT '시작 시점 오프셋 단위',
    duration_value INT NOT NULL COMMENT '기간 값',
    duration_unit VARCHAR(10) NOT NULL COMMENT '기간 단위: week, month',
    title VARCHAR(200) NOT NULL COMMENT '추천 항목 제목',
    content TEXT NOT NULL COMMENT '추천 항목 상세 내용',
    target_amount DECIMAL(15, 0) NULL COMMENT '목표 금액',
    amount_type VARCHAR(10) NULL COMMENT '금액 유형: saving, expense, income',
    target_condition TEXT NULL COMMENT '목표 달성 조건',
    next_action TEXT NOT NULL COMMENT '다음 행동 안내',
    citation TEXT NULL COMMENT '근거/출처',

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',

    PRIMARY KEY (recommendations_id),
    KEY idx_recommendations_goal_order (goal_id, order_no),
    KEY idx_recommendations_diagnosis (diagnosis_id),
    KEY idx_recommendations_category (category_id),

    CONSTRAINT uk_recommendations_goal_item
        UNIQUE (goal_id, item_key),

    CONSTRAINT fk_recommendations_category
        FOREIGN KEY (category_id)
        REFERENCES categories (category_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_recommendations_goal
        FOREIGN KEY (goal_id)
        REFERENCES user_goals (goal_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_recommendations_diagnosis
        FOREIGN KEY (diagnosis_id)
        REFERENCES diagnosis (diagnosis_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT ck_recommendations_duration_unit
        CHECK (duration_unit IN ('week', 'month')),

    CONSTRAINT ck_recommendations_amount_type
        CHECK (amount_type IS NULL OR amount_type IN ('saving', 'expense', 'income'))
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='사용자 목표별 AI 추천 로드맵 항목';


CREATE TABLE checklist_items (
    checklist_items_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '체크리스트 항목 식별자',
    recommendations_id BIGINT NOT NULL COMMENT '연관된 추천(로드맵) 항목 ID',
    item_key VARCHAR(100) NOT NULL COMMENT '체크리스트 항목 키',
    contents VARCHAR(255) NULL COMMENT '체크리스트 항목 내용',
    order_no SMALLINT NOT NULL COMMENT '표시 순서',
    estimated_amount DECIMAL(15, 0) NULL COMMENT '예상 금액',
    status VARCHAR(20) NOT NULL COMMENT '진행 상태: todo, done, skipped',
    completed_at DATETIME NULL COMMENT '완료일시',

    PRIMARY KEY (checklist_items_id),
    KEY idx_checklist_items_recommendation_order (recommendations_id, order_no),

    CONSTRAINT uk_checklist_items_recommendation_item
        UNIQUE (recommendations_id, item_key),

    CONSTRAINT fk_checklist_items_recommendation
        FOREIGN KEY (recommendations_id)
        REFERENCES recommendations (recommendations_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT ck_checklist_items_status
        CHECK (status IN ('todo', 'done', 'skipped'))
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='추천 항목별 실행 체크리스트';
