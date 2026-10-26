package dev.chengzhi.qa.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class OpenAiResponseReaderTest {

    @Test
    void readsContentAndUsage() {
        ChatResult result = OpenAiResponseReader.read("""
                {"choices":[{"message":{"content":"plain answer"}}],"usage":{"prompt_tokens":11,"completion_tokens":4}}
                """);

        assertThat(result.content()).contains("plain answer");
        assertThat(result.promptTokens()).isEqualTo(11);
        assertThat(result.completionTokens()).isEqualTo(4);
    }
}
