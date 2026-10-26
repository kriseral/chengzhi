alter table document_chunks
    add column search_vector tsvector
    generated always as (to_tsvector('simple', content)) stored;

create index document_chunks_search_idx
    on document_chunks using gin (search_vector);

create index document_chunks_current_idx
    on document_chunks (document_id, document_version);

create index documents_owner_idx
    on documents (owner_id);

create index document_acl_lookup_idx
    on document_acl (document_id, subject_type, subject_id);
