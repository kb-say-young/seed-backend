-- diagnosis / user_goals / recommendations / checklist_items 로컬·개발 테스트용 샘플 데이터
-- Flyway V14
-- users에는 아직 샘플 데이터를 넣는 마이그레이션이 없어 FK 충족을 위해 테스트용 사용자도 함께 적재한다.
-- 모든 PK는 빈 테이블 기준(AUTO_INCREMENT 시작값 1)으로 명시적으로 지정했다.

SET NAMES utf8mb4;
START TRANSACTION;

INSERT INTO users (user_id, login_id) VALUES
(1, 'testuser1'),
(2, 'testuser2');

INSERT INTO diagnosis (diagnosis_id, user_id, status, summary) VALUES
(1, 1, 'completed', '주거비 부담 완화 및 저축 습관 형성을 위한 진단이 완료되었습니다.'),
(2, 2, 'running', NULL);

INSERT INTO user_goals (goal_id, category_id, user_id, description, flow_type, status, target_amount, target_date, priority_rank) VALUES
(1, '12', 1, JSON_OBJECT('reason', '월세 부담 완화', 'current_rent', 500000), 'mixed', 'active', 3000000, '2026-12-31', 1),
(2, '42', 1, JSON_OBJECT('reason', '비상금 마련'), 'policy_only', 'active', NULL, NULL, 2),
(3, '23', 2, JSON_OBJECT('reason', '자격증 취득'), 'process', 'active', 1000000, '2027-06-30', 1);

INSERT INTO recommendations (recommendations_id, category_id, goal_id, diagnosis_id, item_key, order_no, start_offset_value, start_offset_unit, duration_value, duration_unit, title, content, target_amount, amount_type, target_condition, next_action, citation) VALUES
(1, '12', 1, 1, 'housing_rent_saving', 1, 0, 'week', 3, 'month', '월세 적립 계획 수립', '매월 소득의 일부를 월세 적립 계좌에 자동이체하도록 설정합니다.', 1500000, 'saving', NULL, '적립 전용 계좌를 개설하고 자동이체를 등록하세요.', NULL),
(2, '42', 2, 1, 'emergency_fund_policy', 1, 1, 'week', 6, 'month', '청년 적금 우대 정책 활용', '청년 우대형 적금 상품을 통해 비상금을 마련합니다.', 2000000, 'saving', NULL, '주거래 은행의 청년 적금 상품을 비교해보세요.', NULL),
(3, '23', 3, 2, 'certificate_support', 1, 0, 'month', 4, 'month', '자격증 취득 교육비 지원 신청', '자격증 취득을 위한 교육 지원금 정책을 신청합니다.', 1000000, 'expense', NULL, '관할 고용센터에 교육 지원금을 신청하세요.', NULL);

INSERT INTO checklist_items (checklist_items_id, recommendations_id, item_key, contents, order_no, estimated_amount, status, completed_at) VALUES
(1, 1, 'open_saving_account', '월세 적립 전용 계좌 개설', 1, NULL, 'done', '2026-08-01 10:00:00'),
(2, 1, 'setup_auto_transfer', '자동이체 등록', 2, NULL, 'todo', NULL),
(3, 2, 'compare_saving_products', '청년 적금 상품 비교', 1, NULL, 'todo', NULL),
(4, 3, 'apply_education_fund', '교육 지원금 신청서 제출', 1, 1000000, 'todo', NULL);

COMMIT;
