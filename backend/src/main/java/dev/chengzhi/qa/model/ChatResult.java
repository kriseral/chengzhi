package dev.chengzhi.qa.model;

public record ChatResult(String content, Integer promptTokens, Integer completionTokens) {
}
