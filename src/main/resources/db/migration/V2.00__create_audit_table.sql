CREATE TABLE audit (
    id uuid PRIMARY KEY NOT NULL,
    audit_point TEXT NOT NULL,
    sender_email TEXT,
    payload JSONB,
    created_at timestamp without time zone NOT NULL
);