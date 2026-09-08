-- V12__add_columns_to_users.sql
-- users 기본키를 user_id로 통일하고, 로그인 아이디와 진단(공통 프로필) 컬럼을 추가한다.
--
-- 회원가입 시 login_id/name/birth_date/phone_number를 함께 받으므로 이 넷은 NOT NULL이다.
-- 나머지 프로필 컬럼(protection_end_date 이하)은 이후 진단(intake) 제출 시점에
-- 채워지므로 NOT NULL로 두지 않는다.
--
-- region_code/income은 해커톤 DDL(VARCHAR(20)/VARCHAR(30)) 대신, 이미 구현된
-- 검증 로직(regions.region_code 5자리 대조, 소득 금액 수치 검증)에 맞춰
-- 기존 타입(VARCHAR(5)/BIGINT)을 유지한다.
-- budget/fixed_budget/household_size는 User 엔티티(BigDecimal/Short)에 맞춰 DECIMAL/SMALLINT로 둔다.

ALTER TABLE users
    CHANGE COLUMN id user_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '사용자 식별자';

ALTER TABLE users
    ADD COLUMN login_id VARCHAR(30) NOT NULL COMMENT '로그인 아이디' AFTER user_id,
    ADD CONSTRAINT uk_users_login_id UNIQUE (login_id),

    ADD COLUMN name VARCHAR(20) NOT NULL COMMENT '이름',
    ADD COLUMN birth_date CHAR(8) NOT NULL COMMENT '생년월일(yyyyMMdd)',
    ADD COLUMN phone_number CHAR(11) NOT NULL COMMENT '휴대폰 번호(하이픈 제외, 010XXXXXXXX)',

    ADD COLUMN protection_end_date DATE NULL COMMENT '보호종료(예정)일',
    ADD COLUMN is_youth_support BOOLEAN NULL COMMENT '자립준비청년 신청 여부',
    ADD COLUMN region_code VARCHAR(5) NULL COMMENT '거주지 지역 코드(시/군/구 레벨, regions.region_code)',
    ADD COLUMN income BIGINT NULL COMMENT '월 평균 소득 금액(원)',
    ADD COLUMN household_size SMALLINT NULL COMMENT '가구원 수',
    ADD COLUMN is_basic_recipient BOOLEAN NULL COMMENT '기초생활수급자 여부',

    ADD COLUMN budget DECIMAL(10, 0) NULL COMMENT '배분 가능한 예산, 100억 미만',
    ADD COLUMN fixed_budget DECIMAL(15, 0) NULL COMMENT '디딤씨앗통장(CDA) 잔액',
    ADD COLUMN has_cda BOOLEAN NULL COMMENT '디딤씨앗통장(CDA) 보유 여부',

    ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    ADD COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시';
