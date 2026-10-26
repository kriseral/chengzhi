package dev.chengzhi.qa;

import static org.assertj.core.api.Assertions.assertThat;

import dev.chengzhi.search.RetrievedChunk;
import java.util.List;
import org.junit.jupiter.api.Test;

class CitationGuardTest {

    private final RetrievedChunk evidence = new RetrievedChunk(
            "doc_1:v1:c0", "doc_1", "请假制度", 1, 0, "请假超过三天需要导师审批。");

    @Test
    void acceptsCitationFromCurrentEvidence() {
        ModelAnswer answer = new ModelAnswer("需要导师审批。", List.of(evidence.citationId()), false);

        CitationGuard.GuardResult result = CitationGuard.check(answer, List.of(evidence));

        assertThat(result.refused()).isFalse();
        assertThat(result.citationIds()).containsExactly(evidence.citationId());
    }

    @Test
    void rejectsCitationOutsideCurrentEvidence() {
        ModelAnswer answer = new ModelAnswer("薪资每月十五日发放。", List.of("doc_secret:v1:c0"), false);

        CitationGuard.GuardResult result = CitationGuard.check(answer, List.of(evidence));

        assertThat(result.refused()).isTrue();
        assertThat(result.citationIds()).isEmpty();
    }

    @Test
    void rejectsAnswerWithoutCitation() {
        ModelAnswer answer = new ModelAnswer("我知道答案。", List.of(), false);

        assertThat(CitationGuard.check(answer, List.of(evidence)).refused()).isTrue();
    }
}
