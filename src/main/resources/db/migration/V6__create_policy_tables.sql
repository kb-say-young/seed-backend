-- 정책 도메인 테이블 생성
-- MySQL 8.x / Flyway
--
-- policies.category_id에는 서비스 세부 카테고리 ID(11~42)를 저장한다.
-- 대분류는 categories.parent_category_id로 조회 가능하므로 중복 저장하지 않는다.
-- 정책 상세는 policy_no로 외부 온통청년 API에서 조회한다.

CREATE TABLE policies (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '정책 내부 식별자',
    policy_no CHAR(20) NOT NULL COMMENT '온통청년 정책번호, 외부 상세 API 조회 키',
    name VARCHAR(255) NOT NULL COMMENT '정책명',
    keyword VARCHAR(255) NULL COMMENT '정책 키워드',
    description TEXT NULL COMMENT '정책 설명, 카드 기본 정보',
    support_content TEXT NULL COMMENT '정책 지원 내용',

    category_id VARCHAR(2) NOT NULL COMMENT '서비스 세부 카테고리 ID',

    institution_name VARCHAR(255) NULL COMMENT '주관 기관명',

    min_age INT NULL COMMENT '지원 최소 연령',
    max_age INT NULL COMMENT '지원 최대 연령',
    age_limit BOOLEAN NULL COMMENT '연령 제한 여부',

    income_code VARCHAR(20) NULL COMMENT '소득 조건 구분 코드',
    income_min BIGINT NULL COMMENT '최소 소득 조건, 원 단위',
    income_max BIGINT NULL COMMENT '최대 소득 조건, 원 단위',
    income_etc TEXT NULL COMMENT '기타 소득 조건',

    qualification TEXT NULL COMMENT '추가 신청 자격 조건',
    restriction TEXT NULL COMMENT '참여 제한 및 제외 조건',

    apply_start_date DATE NULL COMMENT '신청 시작일',
    apply_end_date DATE NULL COMMENT '신청 종료일',

    independent_youth BOOLEAN NOT NULL DEFAULT FALSE COMMENT '자립준비청년 관련 정책 여부',

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',

    PRIMARY KEY (id),
    UNIQUE KEY uk_policies_policy_no (policy_no),
    KEY idx_policies_category (category_id),
    KEY idx_policies_age (min_age, max_age),
    KEY idx_policies_income (income_code, income_min, income_max),
    KEY idx_policies_apply_period (apply_start_date, apply_end_date),
    KEY idx_policies_independent_youth (independent_youth),

    CONSTRAINT fk_policies_category
        FOREIGN KEY (category_id)
        REFERENCES categories(category_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='청년정책 필터링 및 카드 기본정보';


CREATE TABLE policy_regions (
    policy_id BIGINT NOT NULL COMMENT '정책 ID',
    region_code VARCHAR(10) NOT NULL COMMENT '적용 지역 코드',

    PRIMARY KEY (policy_id, region_code),
    KEY idx_policy_regions_region_code (region_code),

    CONSTRAINT fk_policy_regions_policy
        FOREIGN KEY (policy_id)
        REFERENCES policies(id)
        ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='정책별 적용 지역';


CREATE TABLE policy_jobs (
    policy_id BIGINT NOT NULL COMMENT '정책 ID',
    job_code VARCHAR(20) NOT NULL COMMENT '취업 상태 조건 코드',

    PRIMARY KEY (policy_id, job_code),
    KEY idx_policy_jobs_job_code (job_code),

    CONSTRAINT fk_policy_jobs_policy
        FOREIGN KEY (policy_id)
        REFERENCES policies(id)
        ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='정책별 취업 상태 조건';


CREATE TABLE policy_schools (
    policy_id BIGINT NOT NULL COMMENT '정책 ID',
    school_code VARCHAR(20) NOT NULL COMMENT '학력 조건 코드',

    PRIMARY KEY (policy_id, school_code),
    KEY idx_policy_schools_school_code (school_code),

    CONSTRAINT fk_policy_schools_policy
        FOREIGN KEY (policy_id)
        REFERENCES policies(id)
        ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='정책별 학력 조건';


CREATE TABLE policy_targets (
    policy_id BIGINT NOT NULL COMMENT '정책 ID',
    target_code VARCHAR(20) NOT NULL COMMENT '특화/취약 대상 조건 코드',

    PRIMARY KEY (policy_id, target_code),
    KEY idx_policy_targets_target_code (target_code),

    CONSTRAINT fk_policy_targets_policy
        FOREIGN KEY (policy_id)
        REFERENCES policies(id)
        ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='정책별 특화/취약 대상 조건';
