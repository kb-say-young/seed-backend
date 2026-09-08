package com.sayyoung.seed.domain.diagnosis.client;

import com.sayyoung.seed.domain.diagnosis.dto.request.DifyWorkflowRequestDto;
import com.sayyoung.seed.domain.diagnosis.exception.DiagnosisErrorCode;
import com.sayyoung.seed.global.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * RestClient 호출을 MockRestServiceServer로 가로채 DifyClient의 응답 처리(원문 JSON 반환,
 * 빈 응답/오류 응답 시 예외 매핑)만 검증한다. 실제 Dify 서버와는 통신하지 않는다.
 */
class DifyClientTest {

    private static final DifyWorkflowRequestDto REQUEST = DifyWorkflowRequestDto.of(
            "{}",
            "[]",
            1L
    );

    private DifyClient newClient(MockRestServiceServer[] serverHolder) {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://localhost");
        serverHolder[0] = MockRestServiceServer.bindTo(builder).build();
        return new DifyClient(builder.build());
    }

    @Test
    void 정상_응답이면_원문_JSON_문자열을_그대로_반환한다() {

        // given
        MockRestServiceServer[] serverHolder = new MockRestServiceServer[1];
        DifyClient difyClient = newClient(serverHolder);
        String rawJson = "{\"roadmap_items\":[]}";
        serverHolder[0].expect(requestTo("/v1/workflows/run"))
                .andRespond(withSuccess(rawJson, MediaType.APPLICATION_JSON));

        // when
        String response = difyClient.run(REQUEST);

        // then
        assertThat(response).isEqualTo(rawJson);
    }

    @Test
    void 응답_바디가_비어있으면_DIFY_RESPONSE_EMPTY_예외를_던진다() {

        // given
        MockRestServiceServer[] serverHolder = new MockRestServiceServer[1];
        DifyClient difyClient = newClient(serverHolder);
        serverHolder[0].expect(requestTo("/v1/workflows/run"))
                .andRespond(withSuccess("", MediaType.APPLICATION_JSON));

        // when & then
        assertThatThrownBy(() -> difyClient.run(REQUEST))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(DiagnosisErrorCode.DIFY_RESPONSE_EMPTY);
    }

    @Test
    void 서버_오류_응답이면_DIFY_API_CALL_FAILED_예외를_던진다() {

        // given
        MockRestServiceServer[] serverHolder = new MockRestServiceServer[1];
        DifyClient difyClient = newClient(serverHolder);
        serverHolder[0].expect(requestTo("/v1/workflows/run"))
                .andRespond(withServerError());

        // when & then
        assertThatThrownBy(() -> difyClient.run(REQUEST))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(DiagnosisErrorCode.DIFY_API_CALL_FAILED);
    }
}
