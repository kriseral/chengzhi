package dev.chengzhi.search;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class HybridRankerTest {

    @Test
    void sharedEvidenceRanksAheadOfSingleSourceEvidence() {
        RetrievedChunk shared = chunk("shared");
        RetrievedChunk lexicalOnly = chunk("lexical");
        RetrievedChunk vectorOnly = chunk("vector");

        List<RetrievedChunk> fused = HybridRanker.fuse(
                List.of(lexicalOnly, shared),
                List.of(vectorOnly, shared),
                3);

        assertThat(fused).extracting(RetrievedChunk::citationId)
                .containsExactly("shared", "lexical", "vector");
    }

    private static RetrievedChunk chunk(String id) {
        return new RetrievedChunk(id, "doc", "title", 1, 0, id);
    }
}
