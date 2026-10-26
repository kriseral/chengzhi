package dev.chengzhi.qa.model;

import static org.assertj.core.api.Assertions.assertThat;

import dev.chengzhi.config.AppProperties;
import dev.chengzhi.qa.ExtractiveAnswerModel;
import dev.chengzhi.qa.ModelAnswer;
import dev.chengzhi.search.RetrievedChunk;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;

class RoutingAnswerModelTest {

    private final RetrievedChunk evidence = new RetrievedChunk(
            "doc_1:v1:c0", "doc_1", "实验室制度", 1, 0, "进入实验室前必须完成安全培训。");

    @Test
    void usesExtractiveFallbackWhenModelIsNotConfigured() {
        RoutingAnswerModel model = new RoutingAnswerModel(properties(false, ""), new ExtractiveAnswerModel(), unavailableRemote());

        ModelAnswer answer = model.answer("进入实验室前必须做什么", List.of(evidence));

        assertThat(answer.refused()).isFalse();
        assertThat(answer.citationIds()).containsExactly(evidence.citationId());
    }

    @Test
    void refusesWhenConfiguredModelFails() {
        RoutingAnswerModel model = new RoutingAnswerModel(properties(true, "test-key"), new ExtractiveAnswerModel(), unavailableRemote());

        ModelAnswer answer = model.answer("问题", List.of(evidence));

        assertThat(answer.refused()).isTrue();
        assertThat(answer.answer()).isEqualTo(RoutingAnswerModel.UNAVAILABLE);
        assertThat(answer.citationIds()).isEmpty();
    }

    private OpenAiCompatibleAnswerModel unavailableRemote() {
        return new OpenAiCompatibleAnswerModel(properties(true, "test-key").qa().model(), command -> {
            throw new ModelClientException("timeout");
        });
    }

    private AppProperties properties(boolean enabled, String apiKey) {
        return new AppProperties(
                new AppProperties.Auth(Duration.ofHours(8)),
                new AppProperties.Ingest(400, 60),
                new AppProperties.Qa(4, new AppProperties.Model(
                        enabled, "https://proxy.example/v1", apiKey, "gpt-test", Duration.ofSeconds(20), "evidence-v1")));
    }
}
