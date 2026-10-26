package dev.chengzhi.ingest;

import java.util.ArrayList;
import java.util.List;

public final class Chunker {

    private Chunker() {
    }

    public static List<String> split(String content, int size, int overlap) {
        if (size <= 0 || overlap < 0 || overlap >= size) {
            throw new IllegalArgumentException("invalid chunk window");
        }
        String normalized = content.replace("\r\n", "\n").trim();
        List<String> chunks = new ArrayList<>();
        if (normalized.isEmpty()) {
            return chunks;
        }
        int start = 0;
        while (start < normalized.length()) {
            int end = Math.min(start + size, normalized.length());
            String chunk = normalized.substring(start, end).trim();
            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }
            if (end == normalized.length()) {
                break;
            }
            start = end - overlap;
        }
        return List.copyOf(chunks);
    }
}
