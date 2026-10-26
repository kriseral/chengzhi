package dev.chengzhi.qa;

import dev.chengzhi.search.RetrievedChunk;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class CitationGuard {

    public static final String INSUFFICIENT_EVIDENCE = "知识库中没有足够证据回答这个问题。";

    private CitationGuard() {
    }

    public static GuardResult check(ModelAnswer answer, List<RetrievedChunk> evidence) {
        if (evidence.isEmpty()) {
            return GuardResult.refuse(INSUFFICIENT_EVIDENCE);
        }
        if (answer.refused()) {
            return GuardResult.refuse(textOrDefault(answer.answer(), INSUFFICIENT_EVIDENCE));
        }
        if (answer.citationIds() == null || answer.citationIds().isEmpty()) {
            return GuardResult.refuse("回答没有引用本次检索到的证据。");
        }
        Set<String> allowed = new LinkedHashSet<>();
        for (RetrievedChunk chunk : evidence) {
            allowed.add(chunk.citationId());
        }
        for (String citationId : answer.citationIds()) {
            if (!allowed.contains(citationId)) {
                return GuardResult.refuse("回答引用了本次检索结果之外的证据。");
            }
        }
        String text = textOrDefault(answer.answer(), "");
        if (text.isEmpty()) {
            return GuardResult.refuse("回答没有引用本次检索到的证据。");
        }
        return new GuardResult(text, List.copyOf(new LinkedHashSet<>(answer.citationIds())), false);
    }

    private static String textOrDefault(String value, String fallback) {
        String text = value == null ? "" : value.trim();
        return text.isEmpty() ? fallback : text;
    }

    public record GuardResult(String answer, List<String> citationIds, boolean refused) {

        static GuardResult refuse(String reason) {
            return new GuardResult(reason, List.of(), true);
        }
    }
}
