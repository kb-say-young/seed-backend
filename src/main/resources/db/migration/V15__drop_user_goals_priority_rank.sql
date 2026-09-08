-- 목표 간 우선순위 기능 폐지. 기존 진행 방식과 상태는 유지한다.
ALTER TABLE user_goals DROP COLUMN priority_rank;
