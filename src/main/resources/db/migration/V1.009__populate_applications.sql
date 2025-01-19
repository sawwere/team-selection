INSERT INTO applications (id, student_id, team_id, status) VALUES
    (1, 19, 4, 'accepted'),
    (2, 16, 3, 'accepted'),
    (3, 12, 1, 'accepted'),
    (12, 18, 1, 'cancelled'),
    (11, 17, 3, 'cancelled'),
    (10, 19, 2, 'cancelled'),
    (9, 16, 3, 'rejected'),
    (8, 11, 2, 'rejected'),
    (7, 16, 4, 'rejected'),
    (6, 6, 2, 'sent'),
    (5, 5, 1, 'sent'),
    (4, 1, 3, 'sent');

SELECT pg_catalog.setval('applications_id_seq', 12, true);
