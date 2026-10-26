package dev.chengzhi.search;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class HybridRanker {

    private HybridRanker() {
    }

    public static List<RetrievedChunk> fuse(List<RetrievedChunk> lexical, List<RetrievedChunk> vector, int limit) {
        Map<String, Ranked> ranked = new LinkedHashMap<>();
        add(ranked, lexical, 1.0);
        add(ranked, vector, 1.0);
        return ranked.values().stream()
                .sorted((left, right) -> {
                    int score = Double.compare(right.score, left.score);
                    if (score != 0) {
                        return score;
                    }
                    return left.chunk.citationId().compareTo(right.chunk.citationId());
                })
                .limit(Math.max(limit, 0))
                .map(item -> item.chunk)
                .toList();
    }

    private static void add(Map<String, Ranked> ranked, List<RetrievedChunk> chunks, double weight) {
        for (int index = 0; index < chunks.size(); index++) {
            RetrievedChunk chunk = chunks.get(index);
            double score = weight / (index + 1);
            ranked.compute(chunk.citationId(), (id, existing) -> {
                if (existing == null) {
                    return new Ranked(chunk, score);
                }
                existing.score += score;
                return existing;
            });
        }
    }

    private static final class Ranked {
        private final RetrievedChunk chunk;
        private double score;

        private Ranked(RetrievedChunk chunk, double score) {
            this.chunk = chunk;
            this.score = score;
        }
    }
}
