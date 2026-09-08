-- V21__add_education_to_users.sql
-- users 테이블에 학력 컬럼 추가
-- 값: 고졸미만, 고교재학, 고졸예정, 고교졸업, 대학재학, 대졸예정, 대학졸업, 석박사, 기타
--
-- V14에 이미 학력 없는 테스트 사용자 데이터가 있어 NOT NULL로 바로 추가할 수 없다.
-- NULL 허용으로 추가 → 기존 행 백필 → NOT NULL 전환 순서로 안전하게 처리한다.

ALTER TABLE users
    ADD COLUMN education VARCHAR(10) NULL COMMENT '학력' AFTER phone_number;

UPDATE users SET education = '기타' WHERE education IS NULL;

ALTER TABLE users
    MODIFY COLUMN education VARCHAR(10) NOT NULL COMMENT '학력',
    ADD CONSTRAINT ck_users_education
        CHECK (education IN ('고졸미만', '고교재학', '고졸예정', '고교졸업', '대학재학', '대졸예정', '대학졸업', '석박사', '기타'));
