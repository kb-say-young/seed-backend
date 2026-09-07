package com.sayyoung.seed.global.utils;

import com.sayyoung.seed.domain.diagnosis.dto.response.DifyWorkflowResponseDto;
import com.sayyoung.seed.domain.diagnosis.dto.RoadmapItemDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DifyResponseParserTest {

    @Test
    void 정상적인_로드맵_응답_JSON을_파싱한다() {

        // given
        String json = """
                {
                  "roadmap_items": [
                    {
                      "item_key": "housing_rent_saving",
                      "origin_sub_category": "월세",
                      "order_no": 1,
                      "start_offset": { "value": 0, "unit": "week" },
                      "duration": { "value": 3, "unit": "month" },
                      "title": "월세 적립 계획 수립",
                      "content": "매월 소득의 일부를 월세 적립 계좌에 자동이체하도록 설정합니다.",
                      "target_amount": 1500000,
                      "amount_type": "saving",
                      "target_condition": null,
                      "next_action": "적립 전용 계좌를 개설하고 자동이체를 등록하세요.",
                      "citation": null,
                      "checklist": [
                        {
                          "item_key": "open_saving_account",
                          "content": "월세 적립 전용 계좌 개설",
                          "amount_type": null,
                          "estimated_amount": null
                        }
                      ]
                    }
                  ]
                }
                """;

        // when
        DifyWorkflowResponseDto response = DifyResponseParser.parse(json);

        // then
        assertThat(response.getRoadmapItems()).hasSize(1);

        RoadmapItemDto item = response.getRoadmapItems().get(0);
        assertThat(item.getItemKey()).isEqualTo("housing_rent_saving");
        assertThat(item.getOriginSubCategory()).isEqualTo("월세");
        assertThat(item.getStartOffset().getValue()).isEqualTo(0);
        assertThat(item.getStartOffset().getUnit()).isEqualTo("week");
        assertThat(item.getDuration().getValue()).isEqualTo(3);
        assertThat(item.getDuration().getUnit()).isEqualTo("month");
        assertThat(item.getChecklist()).hasSize(1);
        assertThat(item.getChecklist().get(0).getItemKey()).isEqualTo("open_saving_account");
    }

    @Test
    void 형식이_잘못된_JSON은_파싱_예외를_던진다() {

        // given
        String malformedJson = "{ \"roadmap_items\": [ ";

        // when & then
        assertThatThrownBy(() -> DifyResponseParser.parse(malformedJson))
                .isInstanceOf(DifyResponseParseException.class);
    }
}
