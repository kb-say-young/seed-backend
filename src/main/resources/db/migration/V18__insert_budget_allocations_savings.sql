-- budget_allocations / savings 로컬·개발 테스트용 샘플 데이터
-- Flyway V18 (대상: V17에서 생성한 테이블)
-- V14에서 적재한 사용자(user_id 1,2) / 진단(diagnosis_id 1,2)에 이어지는 데이터로,
-- 새 값을 만들지 않고 그대로 참조한다.

SET NAMES utf8mb4;
START TRANSACTION;

INSERT INTO budget_allocations (user_id, latest_diagnosis_id, category_id, ai_ratio, ai_amount, user_ratio, user_amount) VALUES
(1, 1, '12', 40.00, 1200000, 45.00, 1350000),
(1, 1, '42', 20.00, 600000, NULL, NULL),
(2, 2, '23', 30.00, 300000, NULL, NULL);

INSERT INTO savings (category_id, user_id, title, amount, saved_at) VALUES
('12', 1, '3월 월세 적립', 500000, '2026-08-05'),
('12', 1, '4월 월세 적립', 500000, '2026-09-05'),
('42', 1, '청년 적금 자동이체', 300000, '2026-09-01'),
('23', 2, '자격증 취득 교육비 저축', 200000, '2026-09-03');

COMMIT;
