-- V3__create_users.sql
-- 사용자 기본 정보 테이블

CREATE TABLE users
(
    id BIGINT NOT NULL AUTO_INCREMENT
        COMMENT '사용자 식별자',

    PRIMARY KEY (id)
) COMMENT = '사용자 정보';