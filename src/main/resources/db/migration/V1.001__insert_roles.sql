INSERT INTO roles(id, name) VALUES
    (1, 'USER'),
    (2, 'JURY'),
    (3, 'ADMIN')
    ON CONFLICT DO NOTHING;