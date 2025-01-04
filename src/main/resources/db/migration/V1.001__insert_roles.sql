INSERT INTO roles(id, name) VALUES
    (1, 'USER'),
    (2, 'JURY'),
    (3, 'ADMIN'),
    (4, 'STUDENT')
    ON CONFLICT DO NOTHING;

SELECT pg_catalog.setval('public.roles_id_seq', 4, false);
