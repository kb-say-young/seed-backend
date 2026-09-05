-- 카테고리 테이블 생성
-- MySQL 8.x / Flyway

CREATE TABLE categories (
    category_id VARCHAR(2) NOT NULL COMMENT '카테고리 ID',
    parent_category_id VARCHAR(2) NULL COMMENT '상위 카테고리 ID',
    name VARCHAR(50) NOT NULL COMMENT '카테고리명',

    PRIMARY KEY (category_id),
    KEY idx_categories_parent (parent_category_id),

    CONSTRAINT fk_categories_parent
        FOREIGN KEY (parent_category_id)
        REFERENCES categories(category_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='정책 및 사용자 목표 카테고리';
