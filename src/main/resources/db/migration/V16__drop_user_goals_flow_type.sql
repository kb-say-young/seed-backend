-- 진단 및 정책 매칭에서 사용하지 않는 진행 방식 제거. 목표 상태는 유지한다.
ALTER TABLE user_goals
    DROP CHECK ck_user_goals_flow_type,
    DROP COLUMN flow_type;
