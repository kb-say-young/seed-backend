-- V12__add_login_id_to_users.sql
-- 사용자 로그인 아이디 컬럼 추가

ALTER TABLE users
    ADD COLUMN login_id VARCHAR(30) NOT NULL COMMENT '로그인 아이디' AFTER id,
    ADD CONSTRAINT uq_users_login_id UNIQUE (login_id);
