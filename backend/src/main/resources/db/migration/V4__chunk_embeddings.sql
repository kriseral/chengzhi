alter table document_chunks
    add column embedding real[];

create index document_chunks_embedding_idx
    on document_chunks (document_id, document_version)
    where embedding is not null;
