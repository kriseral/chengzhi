package dev.chengzhi.qa.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class OpenAiResponseReader {

    private static final ObjectMapper JSON = new ObjectMapper();

    private OpenAiResponseReader() {
    }

    public static ChatResult read(String body) {
        try {
            JsonNode root = JSON.readTree(body);
            JsonNode message = root.path("choices").path(0).path("message");
            String content = contentOf(message.get("content"));
            JsonNode usage = root.get("usage");
            return new ChatResult(content, integer(usage, "prompt_tokens"), integer(usage, "completion_tokens"));
        } catch (ModelClientException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new ModelClientException("模型服务响应无法解析", exception);
        }
    }

    private static String contentOf(JsonNode content) {
        if (content == null || content.isNull()) {
            throw new ModelClientException("模型服务响应缺少内容");
        }
        if (content.isTextual()) {
            return content.asText();
        }
        if (content.isArray()) {
            StringBuilder text = new StringBuilder();
            for (JsonNode part : content) {
                if (part.hasNonNull("text")) {
                    text.append(part.get("text").asText());
                }
            }
            if (!text.isEmpty()) {
                return text.toString();
            }
        }
        throw new ModelClientException("模型服务响应缺少文本内容");
    }

    private static Integer integer(JsonNode parent, String field) {
        if (parent == null || !parent.has(field) || parent.get(field).isNull()) {
            return null;
        }
        return parent.get(field).asInt();
    }
}
