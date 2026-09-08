-- 진단 요약 구조화(#37)
-- Diagnosis.summary는 어떤 코드에서도 채우거나 읽지 않는 비정형 TEXT 컬럼이었다.
-- 목표/경과/남은 개월 수, 이번달 목표 저축액은 diagnosis.created_at, recommendations,
-- users.fixed_budget을 조합해 조회 시점에 계산하므로 summary 컬럼은 더 이상 필요 없다.

ALTER TABLE diagnosis
    DROP COLUMN summary;
