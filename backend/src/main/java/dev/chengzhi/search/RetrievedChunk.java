package dev.chengzhi.search;

public record RetrievedChunk(
        String citationId,
        String documentId,
        String title,
        int documentVersion,
        int chunkIndex,
        String content) {
}
