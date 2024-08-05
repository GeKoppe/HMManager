INSERT INTO "users" (
    "user_name",
    "pw",
    "locked",
    "created_at"
) VALUES (
    'test',
    '123',
    'false',
    '2024-05-08 00:00:00.000'
);

INSERT INTO "element_types" (
    "name"
) VALUES (
    'test'
);

INSERT INTO "meta_sets" (
    "name",
    last_changed
) VALUES (
    'test',
    '2024-05-08 00:00:00.000'
);

INSERT INTO "document_paths" (
    "path"
) VALUES (
    'C:\temp\hmdms\archive'
);

INSERT INTO "elements" (
    "guid",
    "name",
    "meta_set_id",
    "internal_date",
    "creator",
    "type",
    "parent_id",
    "document_id"
) VALUES (
    'abc124',
    'Hello World',
    1,
    '2024-05-08 00:00:00.000',
    1,
    1,
    1,
    'test'
), (
    'abc125',
    'Hello World',
    1,
    '2024-05-08 00:00:00.000',
    1,
    1,
    1,
    'test'
), (
    'abc126',
    'Hello World',
    1,
    '2024-05-08 00:00:00.000',
    1,
    1,
    1,
    'test'
), (
    'abc1278',
    'Hello World',
    1,
    '2024-05-08 00:00:00.000',
    1,
    1,
    1,
    'test'
), (
    'abc127',
    'Hello World',
    1,
    '2024-05-08 00:00:00.000',
    1,
    1,
    1,
    'test'
), (
    'abc128',
    'Hello World',
    1,
    '2024-05-08 00:00:00.000',
    1,
    1,
    1,
    'test'
);

INSERT INTO "documents" (
    "id",
    "element_id",
    "version",
    "document_path",
    "document_date"
) VALUES (
    'test',
    1,
    1,
    1,
    '2024-05-08 00:00:00.000'
);