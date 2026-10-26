create or replace function chengzhi_search_text(source text)
returns text
language sql
immutable
as $$
    select trim(both ' ' from regexp_replace(
        coalesce(source, ''),
        '([0-9A-Za-z]+|[\u4e00-\u9fff])',
        ' \1 ',
        'g'))
$$;

alter table document_chunks drop column search_vector;

alter table document_chunks
    add column search_vector tsvector
    generated always as (to_tsvector('simple', chengzhi_search_text(content))) stored;

create index document_chunks_search_idx
    on document_chunks using gin (search_vector);
