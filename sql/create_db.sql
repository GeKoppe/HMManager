-- Create the database
-- CREATE DATABASE hmdms;

-- AUTH TABLES

-- Table containing all users
CREATE TABLE "users" (
    user_id int PRIMARY KEY,
    user_name varchar(255) NOT NULL UNIQUE,
    pw varchar(255) NOT NULL,
    locked boolean NOT NULL,
    created_at timestamp NOT NULL
);

CREATE TABLE tickets (
    ticket varchar(255) PRIMARY KEY,
    user_id int NOT NULL,
    issued_at timestamp NOT NULL,
    valid_thru timestamp NOT NULL
);



-- META TABLES
-- Elements in the System
CREATE TABLE "elements" (
    id int PRIMARY KEY,
    "guid" varchar(255) NOT NULL UNIQUE,
    "name" varchar(255) NOT NULL,
    parent_id int NOT NULL,
    meta_set_id int NOT NULL,
    "type" int NOT NULL,
    document_id varchar(255),
    internal_date timestamp NOT NULL,
    creator int NOT NULL
);

CREATE TABLE meta_sets (
    id int PRIMARY KEY,
    "name" varchar(255) NOT NULL UNIQUE,
    last_changed timestamp NOT NULL
);

CREATE TABLE meta_keys (
    id int PRIMARY KEY, -- id of the meta key
    "name" varchar(255) NOT NULL UNIQUE, -- name of the meta key, indexed
    "type" varchar(255) NOT NULL, -- type of the meta key (date, string etc)
    last_changed date NOT NULL -- time this was last edited
);

CREATE TABLE meta_set_keys (
    meta_set_id int NOT NULL, -- Reference to meta_sets(id)
    meta_key_id int NOT NULL, -- Reference to meta_keys(id)
    "index" int NOT NULL -- index of the meta key in the meta set
);

-- Table containing meta values for each element
CREATE TABLE meta_values (
    element_id int NOT NULL, -- Reference to elements(id)
    meta_key_id int NOT NULL, -- Reference to meta_keys(id)
    "value" varchar(255) NOT NULL
);

CREATE TABLE element_types (
    id int PRIMARY KEY,
    "name" varchar(255) NOT NULL UNIQUE
);

CREATE TABLE "documents" (
    id varchar(255) PRIMARY KEY,
    element_id int NOT NULL,
    "version" float NOT NULL,
    document_path int NOT NULL,
    document_date timestamp NOT NULL
);

CREATE TABLE document_paths (
    id int PRIMARY KEY,
    path varchar(255) NOT NULL UNIQUE
);

-- ADD KEYS to tickets
ALTER TABLE tickets
	ADD CONSTRAINT FK_tick_user_id FOREIGN KEY (user_id) REFERENCES "users"(user_id);

-- ADD KEYS to elements
ALTER TABLE "elements"
    ADD CONSTRAINT FK_el_ms FOREIGN KEY (meta_set_id) REFERENCES meta_sets(id);

ALTER TABLE "elements"
    ADD CONSTRAINT FK_el_doc FOREIGN KEY (document_id) REFERENCES "documents"(id);

ALTER TABLE "elements"
    ADD CONSTRAINT FK_el_type FOREIGN KEY ("type") REFERENCES element_types(id);

ALTER TABLE "elements"
    ADD CONSTRAINT FK_el_parent FOREIGN KEY (parent_id) REFERENCES "elements"(id);

ALTER TABLE "elements"
    ADD CONSTRAINT FK_el_creator FOREIGN KEY (creator) REFERENCES "users"(user_id);

-- ADD KEYS TO meta_set_keys
ALTER TABLE meta_set_keys
    ADD CONSTRAINT PK_meta_set_keys PRIMARY KEY (meta_set_id, meta_key_id);

ALTER TABLE meta_set_keys
    ADD CONSTRAINT FK_msk_ms FOREIGN KEY (meta_set_id) REFERENCES meta_sets(id);

ALTER TABLE meta_set_keys
    ADD CONSTRAINT FK_msk_mk FOREIGN KEY (meta_key_id) REFERENCES meta_keys(id);



-- ADD KEYS TO meta_values
ALTER TABLE meta_values
    ADD CONSTRAINT PK_meta_values PRIMARY KEY (element_id, meta_key_id);

ALTER TABLE meta_values
    ADD CONSTRAINT FK_mv_el FOREIGN KEY (element_id) REFERENCES "elements"(id);

ALTER TABLE meta_values
    ADD CONSTRAINT FK_mv_mk FOREIGN KEY (meta_key_id) REFERENCES meta_keys(id);


-- ADD KEYS TO documents
ALTER TABLE "documents"
    ADD CONSTRAINT FK_doc_el FOREIGN KEY (element_id) REFERENCES "elements"(id);

ALTER TABLE "documents"
    ADD CONSTRAINT FK_doc_path FOREIGN KEY (document_path) REFERENCES document_paths(id);


-- ADD INDICES elements
CREATE INDEX I_el_guid
ON "elements" ("guid");

CREATE INDEX I_el_parent
ON "elements" ("parent_id");

CREATE INDEX I_el_name
ON "elements" ("name");

-- ADD INDICES meta_keys
CREATE INDEX I_mk_name
ON meta_keys ("name");

-- ADD INDICES meta_sets
CREATE INDEX I_ms_name
ON meta_sets ("name");

-- ADD INDICES users
CREATE INDEX I_users_name
ON "users" (user_name);

-- ADD INDICES documents
CREATE INDEX I_document_el
ON "documents" (element_id);