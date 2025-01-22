INSERT
INTO
  teams
  (id, captain_id, is_full, name, project_description, project_type_id, quantity_of_students, current_track_id, created_at, updated_at)
VALUES
    (1003, 1, false, 'Almost full', 'мобильное приложение', 1, 6, 1, '2024-11-21 20:24:36.402366', '2024-11-21 20:24:36.402366'),
    (1004, 2, true, 'Old name xQc', 'мобильное приложение', 1, 7, 1, '2024-11-21 20:24:36.402366', '2024-11-21 20:24:36.402366')
ON CONFLICT DO NOTHING;

UPDATE students
SET current_team_id = 1004, has_team = true
WHERE id = 2;

INSERT
INTO
  teams_students
  (team_id, student_id)
VALUES
    (1003, 1),
    (1003, 12),
    (1003, 11),
    (1003, 4),
    (1003, 5),
    (1003, 6),
    (1004, 1),
    (1004, 2),
    (1004, 3),
    (1004, 4),
    (1004, 5),
    (1004, 6),
    (1004, 7)
ON CONFLICT DO NOTHING;

INSERT INTO applications
    (id, team_id, student_id, status)
VALUES
    (1004, 1004, 9, 'sent');