-- V15__alter_users_birth_date_to_date.sql

ALTER TABLE users
    ADD COLUMN birth_date_new DATE NULL COMMENT '생년월일';

UPDATE users
SET birth_date_new = STR_TO_DATE(birth_date, '%Y%m%d');

ALTER TABLE users
    MODIFY COLUMN birth_date_new DATE NOT NULL;

ALTER TABLE users
DROP COLUMN birth_date;

ALTER TABLE users
    CHANGE COLUMN birth_date_new birth_date DATE NOT NULL COMMENT '생년월일';