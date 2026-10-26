package dev.chengzhi.qa.model;

import java.time.Duration;

public record ChatCommand(
        String baseUrl,
        String apiKey,
        String model,
        String systemPrompt,
        String userPrompt,
        Duration timeout) {
}
