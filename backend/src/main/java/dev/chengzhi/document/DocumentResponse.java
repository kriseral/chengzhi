package dev.chengzhi.document;

import java.time.Instant;
import java.util.List;

public record DocumentResponse(
        String id,
        String title,
        String ownerId,
        int version,
        Instant updatedAt,
        int chunkCount,
        List<GrantResponse> grants) {

    public record GrantResponse(String subjectType, String subjectId) {
    }
}
