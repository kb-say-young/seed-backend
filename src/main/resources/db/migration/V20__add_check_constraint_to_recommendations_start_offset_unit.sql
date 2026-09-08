-- 진단 요약 구조화(#37) 코드 리뷰 반영
-- recommendations.duration_unit에는 CHECK 제약이 있지만 start_offset_unit에는 없어서,
-- LLM 응답이 예상치 못한 단위 문자열을 내려줘도 그대로 저장될 수 있었다.
-- DiagnosisSummaryService의 주/개월 환산 로직이 신뢰할 수 있는 값만 받도록 duration_unit과
-- 동일한 제약을 추가한다.

ALTER TABLE recommendations
    ADD CONSTRAINT ck_recommendations_start_offset_unit
        CHECK (start_offset_unit IN ('week', 'month'));
