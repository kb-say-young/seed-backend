package com.sayyoung.seed.domain.diagnosis.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Dify Workflow API 호출에 사용하는 요청 DTO.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DifyWorkflowRequestDto {

    private static final String RESPONSE_MODE = "blocking";
    private static final String USER_PREFIX = "seed-user-";

    private Inputs inputs;

    // blocking 방식으로 응답 수신
    @JsonProperty("response_mode")
    private String responseMode;

    // Dify 로그 추적용 사용자 식별자
    private String user;

    /**
     * Dify에 전달할 실제 입력 데이터.
     */
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Inputs {

        // user_profile을 JSON 문자열로 변환한 값
        @JsonProperty("user_context")
        private String userContext;

        // goals 배열을 JSON 문자열로 변환한 값
        @JsonProperty("goals_input")
        private String goalsInput;

        public static Inputs of(
                String userContext,
                String goalsInput
        ) {
            return new Inputs(
                    userContext,
                    goalsInput
            );
        }
    }

    public static DifyWorkflowRequestDto of(
            String userContext,
            String goalsInput,
            Long userId
    ) {
        return new DifyWorkflowRequestDto(
                Inputs.of(userContext, goalsInput),
                RESPONSE_MODE,
                USER_PREFIX + userId
        );
    }
}
