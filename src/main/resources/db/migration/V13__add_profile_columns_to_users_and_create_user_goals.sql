-- V13__add_profile_columns_to_users_and_create_user_goals.sql
-- 진단(공통 프로필) 컬럼을 users 테이블에 추가하고, 목표 저장용 user_goals 테이블 생성
-- 컬럼명은 카테고리별_요청_API_계약서.md §3.1을 따른다.

ALTER TABLE users
    ADD COLUMN protection_end_date DATE NULL
        COMMENT '보호종료(예정)일',

    ADD COLUMN is_youth_support BOOLEAN NULL
        COMMENT '자립준비청년 신청 여부',

    ADD COLUMN fixed_budget BIGINT NULL
        COMMENT '디딤씨앗통장(CDA) 잔액',

    ADD COLUMN region_code VARCHAR(5) NULL
        COMMENT '거주지 지역 코드(시/군/구 레벨)',

    ADD COLUMN income BIGINT NULL
        COMMENT '월 평균 소득 금액(원)',

    ADD COLUMN is_basic_recipient BOOLEAN NULL
        COMMENT '기초생활수급자 여부',

    ADD COLUMN household_size INT NULL
        COMMENT '가구원 수';

CREATE TABLE user_goals
(
    id BIGINT NOT NULL AUTO_INCREMENT
        COMMENT '목표 식별자',

    user_id BIGINT NOT NULL
        COMMENT '사용자 식별자',

    category_id VARCHAR(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL
        COMMENT '세부 카테고리 코드(categories.category_id)',

    description JSON NOT NULL
        COMMENT '사용자가 작성한 세부목표',

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        COMMENT '생성일시',

    PRIMARY KEY (id),
    KEY idx_user_goals_user (user_id),
    KEY idx_user_goals_category (category_id),

    CONSTRAINT fk_user_goals_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_user_goals_category
        FOREIGN KEY (category_id)
        REFERENCES categories(category_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) COMMENT = '사용자 목표';
