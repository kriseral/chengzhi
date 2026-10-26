package dev.chengzhi.qa.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

class ModelAnswerParserTest {

    @Test
    void parsesJsonObject() {
        var answer = ModelAnswerParser.parse("""
                {"answer":"需要审批","citationIds":["doc_1:v1:c0"],"refused":false}
                """);

        assertThat(answer.answer()).isEqualTo("需要审批");
        assertThat(answer.citationIds()).containsExactly("doc_1:v1:c0");
        assertThat(answer.refused()).isFalse();
    }

    @Test
    void extractsJsonFromMarkdownFence() {
        var answer = ModelAnswerParser.parse("""
                ```json
                {"answer":"证据不足","citationIds":[],"refused":true}
                ```
                """);

        assertThat(answer.refused()).isTrue();
        assertThat(answer.citationIds()).isEmpty();
    }

    @Test
    void rejectsMissingFields() {
        assertThatThrownBy(() -> ModelAnswerParser.parse("{\"answer\":\"incomplete\"}"))
                .isInstanceOf(ModelClientException.class);
    }
}
