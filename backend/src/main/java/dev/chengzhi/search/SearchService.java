package dev.chengzhi.search;

import dev.chengzhi.acl.AccessScope;
import dev.chengzhi.acl.AclPolicy;
import dev.chengzhi.config.AppProperties;
import dev.chengzhi.qa.model.ModelClientException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchService.class);

    private final ChunkMapper chunks;
    private final EmbeddingClient embeddings;
    private final AppProperties properties;
    private final AclPolicy policy;

    public SearchService(
            ChunkMapper chunks,
            EmbeddingClient embeddings,
            AppProperties properties,
            AclPolicy policy) {
        this.chunks = chunks;
        this.embeddings = embeddings;
        this.properties = properties;
        this.policy = policy;
    }

    @Transactional(readOnly = true)
    public List<RetrievedChunk> search(AccessScope scope, String query, int limit) {
        String normalized = query == null ? "" : query.trim();
        if (normalized.isEmpty() || limit <= 0) {
            return List.of();
        }
        List<RetrievedChunk> lexical = chunks.searchAccessible(scope, normalized, limit);
        if (!properties.embedding().configured()) {
            return List.copyOf(lexical);
        }
        try {
            String vector = VectorLiteral.toSql(embeddings.embed(normalized));
            List<RetrievedChunk> semantic = chunks.searchAccessibleByVector(scope, vector, limit);
            return HybridRanker.fuse(lexical, semantic, limit);
        } catch (ModelClientException exception) {
            log.info("embedding retrieval skipped reason={}", exception.getMessage());
            return List.copyOf(lexical);
        }
    }

    AclPolicy policy() {
        return policy;
    }
}
