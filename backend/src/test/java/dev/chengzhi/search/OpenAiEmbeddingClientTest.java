package dev.chengzhi.search;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.chengzhi.qa.model.ModelClientException;
import org.junit.jupiter.api.Test;

class OpenAiEmbeddingClientTest {

    @Test
    void readsEmbeddingsInResponseOrder() throws Exception {
        String body = """
                {"data":[
                  {"index":1,"embedding":[0.2,0.4,0.6]},
                  {"index":0,"embedding":[0.1,0.3,0.5]}
                ]}
                """;

        assertThat(OpenAiEmbeddingClient.read(body, 2, 3))
                .containsExactly(new float[] {0.1f, 0.3f, 0.5f}, new float[] {0.2f, 0.4f, 0.6f});
    }

    @Test
    void rejectsUnexpectedDimensions() {
        assertThatThrownBy(() -> OpenAiEmbeddingClient.read("{\"data\":[{\"index\":0,\"embedding\":[1,2]}]}", 1, 3))
                .isInstanceOf(ModelClientException.class)
                .hasMessage("向量维度与配置不一致");
    }
}
