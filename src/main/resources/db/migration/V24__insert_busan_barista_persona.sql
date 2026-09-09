-- 부산 거주 만 20세, 보호종료 직후 자립준비청년 페르소나 샘플 데이터
-- Flyway V24
--
-- 페르소나: 부산 거주, 만 20세, 어제(조회 시점 기준) 보호기간 종료.
-- 부산에서 전셋집 마련을 원하고, 바리스타가 되는 것이 꿈이며, 현재 생활비 지원이 필요함.
-- 월 소득 90만원, 디딤씨앗통장(CDA) 잔액 800만원.
--
-- 진단은 3개월 전에 완료된 것으로 만들어, 전체 6개월 로드맵 중 일부는 이미 완료,
-- 일부는 진행 중, 일부는 아직 시작 전인 상태를 재현한다.
--
-- V14와 달리 PK를 고정하지 않는다: 이 프로젝트는 테스트가 별도 DB가 아니라 실제
-- 개발 DB에 대해 Flyway를 실행하므로, 이미 다른 사용자/진단 데이터가 쌓인 상태에서
-- 고정 PK를 쓰면 값이 충돌한다. AUTO_INCREMENT로 생성된 PK를 변수로 받아 다음
-- INSERT에서 그대로 참조한다. 날짜도 CURDATE()/NOW() 기준 상대값으로 계산해
-- 마이그레이션이 적용되는 시점과 무관하게 "어제 보호종료", "3개월째 진행"이 유지되도록 한다.

SET NAMES utf8mb4;
START TRANSACTION;

SET @diag_created := DATE_SUB(NOW(), INTERVAL 3 MONTH);
SET @user_created := DATE_SUB(@diag_created, INTERVAL 5 DAY);
SET @protection_end := DATE_SUB(CURDATE(), INTERVAL 1 DAY);
SET @birth_date := DATE_SUB(CURDATE(), INTERVAL 20 YEAR);

-- 사용자: 부산진구(26230) 거주, 자립준비청년, 디딤씨앗통장 800만원, 월소득 90만원
INSERT INTO users (
    login_id, name, phone_number, education, birth_date,
    protection_end_date, is_youth_support, region_code, income,
    household_size, is_basic_recipient, budget, fixed_budget, has_cda,
    created_at, updated_at
) VALUES (
    'busan_barista_dream', '박도윤', '01055512345', '고교졸업', @birth_date,
    @protection_end, TRUE, '26230', 900000,
    1, FALSE, NULL, 8000000, TRUE,
    @user_created, @diag_created
);
SET @user_id := LAST_INSERT_ID();

-- 목표 1: 부산 전셋집 마련 (category 11=전세)
INSERT INTO user_goals (category_id, user_id, description, status, target_amount, target_date, created_at, updated_at)
VALUES ('11', @user_id, JSON_OBJECT(
    'reason', '보호종료 후 부산에서 독립할 전셋집 마련',
    'desired_region', '부산광역시 부산진구',
    'current_housing', '시설 퇴소 후 임시 거주'
), 'active', 30000000, DATE_ADD(@diag_created, INTERVAL 6 MONTH), @diag_created, @diag_created);
SET @goal_jeonse := LAST_INSERT_ID();

-- 목표 2: 바리스타가 되기 위한 직업교육 (category 21=일자리 교육)
INSERT INTO user_goals (category_id, user_id, description, status, target_amount, target_date, created_at, updated_at)
VALUES ('21', @user_id, JSON_OBJECT(
    'reason', '바리스타 자격증 취득 후 카페 취업',
    'desired_job', '바리스타'
), 'active', 600000, DATE_ADD(@diag_created, INTERVAL 6 MONTH), @diag_created, @diag_created);
SET @goal_barista := LAST_INSERT_ID();

-- 목표 3: 보호종료 직후 생활비 지원 (category 32=생활비대출) — 이미 신청·수령까지 끝나 done
INSERT INTO user_goals (category_id, user_id, description, status, target_amount, target_date, created_at, updated_at)
VALUES ('32', @user_id, JSON_OBJECT(
    'reason', '보호종료 직후 생활비 부담 완화'
), 'done', NULL, DATE_ADD(@diag_created, INTERVAL 1 MONTH), @diag_created, DATE_ADD(@diag_created, INTERVAL 25 DAY));
SET @goal_living := LAST_INSERT_ID();

-- 진단: 3개월 전에 완료됨. 조회 시점 기준 3개월째 진행 중.
INSERT INTO diagnosis (user_id, status, created_at, updated_at)
VALUES (@user_id, 'completed', @diag_created, DATE_ADD(@diag_created, INTERVAL 12 SECOND));
SET @diagnosis_id := LAST_INSERT_ID();

-- 로드맵(추천) 항목: 전체 6개월 계획, "3개월째"인 현재 기준으로
-- 0~1개월 구간은 이미 지나 완료, 진행 중인 구간은 절반쯤 진행, 4개월 이후 구간은 아직 시작 전.

-- 목표 1(전세) 항목 1/3: 0~1개월(완료)
INSERT INTO recommendations (
    category_id, goal_id, diagnosis_id, item_key, order_no,
    start_offset_value, start_offset_unit, duration_value, duration_unit,
    title, content, target_amount, amount_type, target_condition, next_action, citation
) VALUES ('11', @goal_jeonse, @diagnosis_id, 'jeonse_plan_and_area_research', 1,
    0, 'month', 1, 'month',
    '전세 자금 계획 수립과 대상지역 조사',
    '부산 내 희망 지역의 전세 시세를 조사하고, 현재 소득과 디딤씨앗통장 잔액을 기준으로 마련 가능한 전세자금 규모를 정리합니다.',
    NULL, NULL,
    '희망 지역과 필요 전세자금 규모를 스스로 정리할 수 있는 상태',
    '부산진구를 포함한 희망 지역 2~3곳의 원룸/소형 전세 시세를 비교해보세요.', NULL);
SET @rec_jeonse_plan := LAST_INSERT_ID();

-- 목표 1(전세) 항목 2/3: 1~4개월(진행중)
INSERT INTO recommendations (
    category_id, goal_id, diagnosis_id, item_key, order_no,
    start_offset_value, start_offset_unit, duration_value, duration_unit,
    title, content, target_amount, amount_type, target_condition, next_action, citation
) VALUES ('11', @goal_jeonse, @diagnosis_id, 'jeonse_deposit_support_application', 2,
    1, 'month', 3, 'month',
    '청년 전세보증금 지원 정책 신청',
    '청년 전세자금대출과 자립준비청년 대상 전세보증금 지원 정책을 상담받고, 필요 서류를 준비해 신청합니다.',
    30000000, 'saving',
    '전세보증금 지원 상담을 마치고 필요 서류를 준비해 신청서를 제출한 상태',
    '청년 전세자금대출 상담을 신청하고, 소득증빙 서류를 미리 준비해두세요.', NULL);
SET @rec_jeonse_apply := LAST_INSERT_ID();

-- 목표 1(전세) 항목 3/3: 4~6개월(아직 시작 전)
INSERT INTO recommendations (
    category_id, goal_id, diagnosis_id, item_key, order_no,
    start_offset_value, start_offset_unit, duration_value, duration_unit,
    title, content, target_amount, amount_type, target_condition, next_action, citation
) VALUES ('11', @goal_jeonse, @diagnosis_id, 'jeonse_contract_and_move_in', 3,
    4, 'month', 2, 'month',
    '매물 계약과 입주 준비',
    '지원이 확정된 자금 범위 내에서 매물을 확인하고 계약한 뒤, 입주와 전입신고를 진행합니다.',
    2000000, 'expense',
    '계약을 마치고 입주 일정과 초기 정착 비용을 확정한 상태',
    '계약 전 등기부등본을 확인하고, 중개수수료와 이사비를 함께 계산해두세요.', NULL);
SET @rec_jeonse_movein := LAST_INSERT_ID();

-- 목표 2(바리스타) 항목 1/2: 0~2개월(완료)
INSERT INTO recommendations (
    category_id, goal_id, diagnosis_id, item_key, order_no,
    start_offset_value, start_offset_unit, duration_value, duration_unit,
    title, content, target_amount, amount_type, target_condition, next_action, citation
) VALUES ('21', @goal_barista, @diagnosis_id, 'barista_certificate_course_enrollment', 1,
    0, 'month', 2, 'month',
    '바리스타 자격증 과정 등록',
    '바리스타 2급 자격증 과정을 검색해 등록하고 교육을 시작합니다.',
    600000, 'expense',
    '자격증 과정 등록을 마치고 교육을 시작한 상태',
    '거주지 근처 바리스타 교육기관 2~3곳을 비교해 등록하세요.', NULL);
SET @rec_barista_course := LAST_INSERT_ID();

-- 목표 2(바리스타) 항목 2/2: 2~6개월(진행중, 시작한 지 1개월)
INSERT INTO recommendations (
    category_id, goal_id, diagnosis_id, item_key, order_no,
    start_offset_value, start_offset_unit, duration_value, duration_unit,
    title, content, target_amount, amount_type, target_condition, next_action, citation
) VALUES ('21', @goal_barista, @diagnosis_id, 'cafe_internship_and_job_support', 2,
    2, 'month', 4, 'month',
    '카페 실습과 취업 지원 프로그램 참여',
    '자격증 취득 후 카페 인턴/실습 프로그램에 참여하고, 청년 취업 지원 프로그램을 함께 신청합니다.',
    NULL, NULL,
    '실습처를 확정하고 취업 지원 프로그램 신청까지 마친 상태',
    '국민취업지원제도 등 청년 취업 지원 프로그램을 함께 신청해보세요.', NULL);
SET @rec_barista_internship := LAST_INSERT_ID();

-- 목표 3(생활비 지원) 항목 1/1: 0~1개월(완료, 이미 신청·수령까지 끝남)
INSERT INTO recommendations (
    category_id, goal_id, diagnosis_id, item_key, order_no,
    start_offset_value, start_offset_unit, duration_value, duration_unit,
    title, content, target_amount, amount_type, target_condition, next_action, citation
) VALUES ('32', @goal_living, @diagnosis_id, 'living_expense_support_application', 1,
    0, 'month', 1, 'month',
    '생활비 지원 정책 신청',
    '보호종료 직후 생활비 부담을 줄이기 위해 자립수당 등 생활비 지원 정책의 신청 자격을 확인하고 신청합니다.',
    NULL, NULL,
    '생활비 지원 신청을 마치고 지원 결정을 받은 상태',
    '자립수당 등 보호종료 청년 대상 생활비 지원 제도의 신청 자격을 확인해보세요.', NULL);
SET @rec_living := LAST_INSERT_ID();

-- 체크리스트: 진단 후 3개월이 지난 현재 시점 기준 진행 상황을 반영
INSERT INTO checklist_items (recommendations_id, item_key, contents, order_no, estimated_amount, status, completed_at) VALUES
-- 전세 계획/조사 (0~1개월, 완료)
(@rec_jeonse_plan, 'busan_jeonse_price_research', '부산 희망 지역 전세 시세 조사', 1, NULL, 'done', DATE_ADD(@diag_created, INTERVAL 2 DAY)),
(@rec_jeonse_plan, 'jeonse_fund_size_estimation', '마련 가능한 전세자금 규모 산정', 2, NULL, 'done', DATE_ADD(@diag_created, INTERVAL 5 DAY)),
-- 전세보증금 지원 신청 (1~4개월, 진행중)
(@rec_jeonse_apply, 'youth_jeonse_deposit_consult', '청년 전세보증금 지원 상담', 1, NULL, 'done', DATE_ADD(@diag_created, INTERVAL 40 DAY)),
(@rec_jeonse_apply, 'document_preparation', '소득증빙, 임대차 관련 서류 준비', 2, NULL, 'done', DATE_ADD(@diag_created, INTERVAL 56 DAY)),
(@rec_jeonse_apply, 'loan_application_submit', '전세보증금 지원 신청서 제출', 3, NULL, 'todo', NULL),
-- 매물 계약/입주 (4~6개월, 아직 시작 전)
(@rec_jeonse_movein, 'property_visit_and_check', '부동산 방문 및 매물 확인', 1, NULL, 'todo', NULL),
(@rec_jeonse_movein, 'lease_contract_review', '임대차계약서 검토 및 계약', 2, NULL, 'todo', NULL),
(@rec_jeonse_movein, 'move_in_and_registration', '입주 및 전입신고', 3, NULL, 'todo', NULL),
-- 바리스타 자격증 과정 (0~2개월, 완료)
(@rec_barista_course, 'barista_course_search_and_enroll', '바리스타 자격증 과정 검색 및 등록', 1, NULL, 'done', DATE_ADD(@diag_created, INTERVAL 4 DAY)),
(@rec_barista_course, 'tuition_payment_and_start', '교육비 결제 및 수강 시작', 2, 600000, 'done', DATE_ADD(@diag_created, INTERVAL 10 DAY)),
-- 카페 실습/취업 지원 (2~6개월, 진행중, 시작한 지 1개월)
(@rec_barista_internship, 'internship_program_search', '카페 인턴/실습 프로그램 탐색', 1, NULL, 'done', DATE_ADD(@diag_created, INTERVAL 75 DAY)),
(@rec_barista_internship, 'internship_matching_apply', '실습처 지원 및 매칭', 2, NULL, 'todo', NULL),
(@rec_barista_internship, 'job_support_program_apply', '청년 취업 지원 프로그램 신청', 3, NULL, 'todo', NULL),
-- 생활비 지원 신청 (0~1개월, 완료)
(@rec_living, 'living_subsidy_eligibility_check', '생활비 지원 신청 자격 확인', 1, NULL, 'done', DATE_ADD(@diag_created, INTERVAL 1 DAY)),
(@rec_living, 'subsidy_application_submit', '생활비 지원 신청서 제출', 2, NULL, 'done', DATE_ADD(@diag_created, INTERVAL 8 DAY));

COMMIT;
