create table app_users (
    id varchar(64) primary key,
    username varchar(64) not null unique,
    password_hash varchar(120) not null,
    department_id varchar(64) not null
);

create table user_roles (
    user_id varchar(64) not null references app_users(id),
    role_name varchar(64) not null,
    primary key (user_id, role_name)
);

create table documents (
    id varchar(64) primary key,
    title varchar(200) not null,
    owner_id varchar(64) not null,
    version integer not null,
    updated_at timestamptz not null
);

create table document_acl (
    id varchar(64) primary key,
    document_id varchar(64) not null references documents(id),
    subject_type varchar(32) not null,
    subject_id varchar(64) not null
);

create table document_chunks (
    id varchar(80) primary key,
    document_id varchar(64) not null,
    document_version integer not null,
    chunk_index integer not null,
    content varchar(4000) not null
);

create index document_chunks_content_idx on document_chunks (document_id, document_version);
