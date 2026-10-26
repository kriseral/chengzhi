package dev.chengzhi.qa.model;

import java.net.URI;

public final class OpenAiEndpoint {

    private OpenAiEndpoint() {
    }

    public static URI chatCompletions(String baseUrl) {
        return endpoint(baseUrl, "/chat/completions");
    }

    public static URI embeddings(String baseUrl) {
        return endpoint(baseUrl, "/embeddings");
    }

    private static URI endpoint(String baseUrl, String suffix) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new ModelClientException("模型服务地址未配置");
        }
        String trimmed = baseUrl.trim().replaceAll("/+$", "");
        if (trimmed.endsWith(suffix)) {
            return URI.create(trimmed);
        }
        return URI.create(trimmed + suffix);
    }
}
