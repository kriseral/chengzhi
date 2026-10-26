package dev.chengzhi.ingest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ChunkerTest {

    @Test
    void splitsLongTextWithOverlap() {
        String content = "a".repeat(10);

        assertThat(Chunker.split(content, 4, 1)).containsExactly("aaaa", "aaaa", "aaaa");
    }

    @Test
    void keepsShortTextAsOneChunk() {
        assertThat(Chunker.split("  请假需要导师审批  ", 20, 4)).containsExactly("请假需要导师审批");
    }
}
