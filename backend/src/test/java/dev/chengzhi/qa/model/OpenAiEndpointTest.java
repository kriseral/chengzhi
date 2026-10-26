package dev.chengzhi.qa.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class OpenAiEndpointTest {

    @Test
    void appendsChatCompletionPath() {
        assertThat(OpenAiEndpoint.chatCompletions("https://proxy.example/v1/").toString())
                .isEqualTo("https://proxy.example/v1/chat/completions");
        @Test
    void appendsEmbeddingsPathAndKeepsExplicitPath() {
        assertThat(OpenAiEndpoint.embeddings("https://proxy.example/v1").toString())
                .isEqualTo("https://proxy.example/v1/embeddings");
        assertThat(OpenAiEndpoint.embeddings("https://proxy.example/v1/embeddings/").toString())
                .isEqualTo("https://proxy.example/v1/embeddings");
    }
}

    @Test
    void keepsExplicitCompletionPath() {
        assertThat(OpenAiEndpoint.chatCompletions("https://proxy.example/v1/chat/completions").toString())
                .isEqualTo("https://proxy.example/v1/chat/completions");
    }
}
