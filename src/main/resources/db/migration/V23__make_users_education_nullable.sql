-- V23__make_users_education_nullable.sql
-- hotfix: 학력을 회원가입이 아닌 진단 정보 제출(submitIntake) 시점에 받도록 변경.
-- 회원가입 시점에는 값이 없으므로 NOT NULL 제약을 해제한다.
-- ck_users_education CHECK 제약은 유지한다 (NULL은 표준 SQL 의미상 CHECK를 통과한다).

ALTER TABLE users
    MODIFY COLUMN education VARCHAR(10) NULL COMMENT '학력';
