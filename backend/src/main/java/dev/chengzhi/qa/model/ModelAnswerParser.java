package dev.chengzhi.qa.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.chengzhi.qa.ModelAnswer;
import java.util.ArrayList;
import java.util.List;

public final class ModelAnswerParser {

    private static final ObjectMapper JSON = new ObjectMapper();

    private ModelAnswerParser() {
    }

    public static ModelAnswer parse(String content) {
        String json = extractJson(content);
        try {
            JsonNode root = JSON.readTree(json);
            if (!root.isObject() || !root.has("answer") || !root.has("citationIds") || !root.has("refused")) {
                throw new ModelClientException("模型输出缺少必需字段");
            }
            List<String> citationIds = new ArrayList<>();
            for (JsonNode citationId : root.get("citationIds")) {
                citationIds.add(citationId.asText());
            }
            return new ModelAnswer(root.get("answer").asText(""), List.copyOf(citationIds), root.get("refused").asBoolean());
        } catch (ModelClientException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new ModelClientException("模型输出不是有效 JSON", exception);
        }
    }

    static String extractJson(String content) {
        if (content == null || content.isBlank()) {
            throw new ModelClientException("模型输出为空");
        }
        String trimmed = content.trim();
        if (trimmed.startsWith("```")) {
            int firstLine = trimmed.indexOf('\n');
            int lastFence = trimmed.lastIndexOf("```");
            if (firstLine >= 0 && lastFence > firstLine) {
                trimmed = trimmed.substring(firstLine + 1, lastFence).trim();
            }
        }
        int start = trimmed.indexOf('{');
        int end = trimmed.lastIndexOf('}');
        if (start < 0 || end <= start) {
            throw new ModelClientException("模型输出不是 JSON 对象");
        }
        return trimmed.substring(start, end + 1);
    }
}
