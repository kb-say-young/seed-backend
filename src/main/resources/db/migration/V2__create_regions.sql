-- V1__create_regions.sql
-- 지역 기준 정보 테이블
-- region_code: 서비스에서 사용하는 5자리 시도/시군구 코드
-- sido_name: 시도명
-- sigungu_name: 시군구명(시도 행은 NULL)
-- parent_code: 시군구의 상위 시도 코드

CREATE TABLE regions (
    region_code VARCHAR(5) NOT NULL
        COMMENT '5자리 지역 코드',

    sido_name VARCHAR(50) NOT NULL
        COMMENT '시도명',

    sigungu_name VARCHAR(50) NULL
        COMMENT '시군구명',

    parent_code VARCHAR(5) NULL
        COMMENT '상위 지역 코드',

    PRIMARY KEY (region_code),

    CONSTRAINT fk_regions_parent
        FOREIGN KEY (parent_code)
        REFERENCES regions(region_code)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    INDEX idx_regions_parent_code (parent_code)
)
COMMENT = '정책 및 사용자 지역 기준 정보';
