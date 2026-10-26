package dev.chengzhi.document;

import dev.chengzhi.acl.AccessScope;
import dev.chengzhi.acl.AclPolicy;
import dev.chengzhi.acl.DocumentAclEntity;
import dev.chengzhi.acl.DocumentAclMapper;
import dev.chengzhi.common.ApiException;
import dev.chengzhi.common.Ids;
import dev.chengzhi.config.AppProperties;
import dev.chengzhi.ingest.Chunker;
import dev.chengzhi.qa.model.ModelClientException;
import dev.chengzhi.search.ChunkEntity;
import dev.chengzhi.search.ChunkMapper;
import dev.chengzhi.search.EmbeddingClient;
import dev.chengzhi.search.VectorLiteral;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);
    private static final Set<String> SUBJECT_TYPES = Set.of("USER", "DEPARTMENT", "ROLE");

    private final DocumentMapper documents;
    private final DocumentAclMapper grants;
    private final ChunkMapper chunks;
    private final EmbeddingClient embeddings;
    private final AclPolicy policy;
    private final AppProperties properties;

    public DocumentService(
            DocumentMapper documents,
            DocumentAclMapper grants,
            ChunkMapper chunks,
            EmbeddingClient embeddings,
            AclPolicy policy,
            AppProperties properties) {
        this.documents = documents;
        this.grants = grants;
        this.chunks = chunks;
        this.embeddings = embeddings;
        this.policy = policy;
        this.properties = properties;
    }

    @Transactional
    public DocumentResponse create(AccessScope actor, DocumentRequest request) {
        return save(actor, Ids.newId("doc"), 1, request);
    }

    @Transactional
    public DocumentResponse update(AccessScope actor, String documentId, DocumentRequest request) {
        DocumentEntity existing = documents.selectById(documentId);
        if (existing == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "文档不存在");
        }
        requireReadable(actor, existing);
        return save(actor, documentId, existing.getVersion() + 1, request);
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> list(AccessScope actor) {
        return documents.selectList(null).stream()
                .filter(document -> policy.canRead(actor, document.getOwnerId(), grants.findByDocumentId(document.getId())))
                .map(this::toResponse)
                .toList();
    }

    private DocumentResponse save(AccessScope actor, String documentId, int version, DocumentRequest request) {
        if (!policy.isApproved()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "当前审批策略为 never，不允许创建/更新文档。");
        }
        List<DocumentRequest.GrantRequest> validGrants = validate(request.grants());
        Instant now = Instant.now();
        DocumentEntity document = documents.selectById(documentId);
        if (document == null) {
            document = new DocumentEntity(documentId, request.title().trim(), actor.userId(), version, now);
            documents.insert(document);
        } else {
            document.publish(request.title().trim(), version, now);
            documents.updateById(document);
        }

        grants.deleteByDocumentId(documentId);
        chunks.deleteByDocumentId(documentId);
        for (DocumentRequest.GrantRequest grant : validGrants) {
            grants.insert(new DocumentAclEntity(
                    Ids.newId("acl"), documentId, grant.subjectType(), grant.subjectId().trim()));
        }
        List<String> parts = Chunker.split(
                request.content(), properties.ingest().chunkSize(), properties.ingest().chunkOverlap());
        List<String> ids = new ArrayList<>();
        for (int index = 0; index < parts.size(); index++) {
            String id = documentId + ":v" + version + ":c" + index;
            ids.add(id);
            chunks.insert(new ChunkEntity(id, documentId, version, index, parts.get(index)));
        }
        storeEmbeddings(ids, parts);
        return toResponse(document);
    }

    private void storeEmbeddings(List<String> ids, List<String> parts) {
        if (!properties.embedding().configured() || parts.isEmpty()) {
            return;
        }
        try {
            List<float[]> vectors = embeddings.embedAll(parts);
            for (int index = 0; index < vectors.size(); index++) {
                chunks.updateEmbedding(ids.get(index), VectorLiteral.toSql(vectors.get(index)));
            }
        } catch (ModelClientException exception) {
            log.info("embedding ingest skipped reason={}", exception.getMessage());
        }
    }

    private List<DocumentRequest.GrantRequest> validate(List<DocumentRequest.GrantRequest> grants) {
        for (DocumentRequest.GrantRequest grant : grants) {
            if (!SUBJECT_TYPES.contains(grant.subjectType())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "授权主体只能是 USER、DEPARTMENT 或 ROLE");
            }
        }
        return grants;
    }

    private void requireReadable(AccessScope actor, DocumentEntity document) {
        if (!policy.canRead(actor, document.getOwnerId(), grants.findByDocumentId(document.getId()))) {
            throw new ApiException(HttpStatus.NOT_FOUND, "文档不存在");
        }
    }

    private DocumentResponse toResponse(DocumentEntity document) {
        List<DocumentResponse.GrantResponse> grantResponses = grants.findByDocumentId(document.getId()).stream()
                .map(grant -> new DocumentResponse.GrantResponse(grant.getSubjectType(), grant.getSubjectId()))
                .toList();
        return new DocumentResponse(
                document.getId(),
                document.getTitle(),
                document.getOwnerId(),
                document.getVersion(),
                document.getUpdatedAt(),
                chunks.countCurrent(document.getId(), document.getVersion()),
                grantResponses);
    }
}
