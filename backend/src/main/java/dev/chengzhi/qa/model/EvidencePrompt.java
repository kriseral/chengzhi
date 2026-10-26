package dev.chengzhi.qa.model;

import dev.chengzhi.search.RetrievedChunk;
import java.util.List;

public final class EvidencePrompt {

    public static final String SYSTEM_PROMPT = """
            你是澄知的受约束问答器。只能依据本次给出的证据回答。
            只返回一个 JSON 对象，不要使用 Markdown。格式必须是：
            {"answer":"中文回答","citationIds":["证据中的 citationId"],"refused":false}
            证据不足时返回 refused=true、空的 citationIds，并且不要猜测。
            citationIds 必须逐字复制证据标题中的 citationId，不能编造。
            """;

    private EvidencePrompt() {
    }

    public static String userPrompt(String question, List<RetrievedChunk> evidence) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("问题：").append(question).append("\n\n证据：\n");
        if (evidence.isEmpty()) {
            prompt.append("（无可见证据）\n");
            return prompt.toString();
        }
        for (RetrievedChunk chunk : evidence) {
            prompt.append("citationId=").append(chunk.citationId())
                    .append("；标题=").append(chunk.title())
                    .append("；版本=").append(chunk.documentVersion())
                    .append("\n")
                    .append(chunk.content())
                    .append("\n\n");
        }
        return prompt.toString();
    }
}
