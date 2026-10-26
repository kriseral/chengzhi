package dev.chengzhi.qa;

import dev.chengzhi.search.RetrievedChunk;
import java.util.List;

public record AskResponse(
        String answer,
        boolean refused,
        List<String> citationIds,
        List<RetrievedChunk> evidence) {
}
