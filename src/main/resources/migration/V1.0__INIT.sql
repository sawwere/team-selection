!-- ИЗМЕНИТЬ, ТЕКУЩЕЕ СОДЕРЖИМОЕ СЛУЖИТ ДЛЯ ПРИМЕРА

CREATE TABLE IF NOT EXISTS roles (
    id bigint NOT NULL,
    name character varying(255)
);

ALTER TABLE roles DROP CONSTRAINT IF EXISTS roles_pkey CASCADE;
ALTER TABLE roles ADD CONSTRAINT roles_pkey PRIMARY KEY (id);