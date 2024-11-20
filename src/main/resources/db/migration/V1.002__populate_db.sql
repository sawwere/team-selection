INSERT
INTO
  users
  (id, is_enabled, is_locked, is_remind_enabled, created_at, updated_at, fio, email, role_id)
VALUES
  (1, TRUE, FALSE, TRUE, NOW(), NOW(), 'admin', 'admin_mail', 3),
  (2, TRUE, FALSE, TRUE, NOW(), NOW(), 'user2', 'user2_mail', 1),
  (3, TRUE, FALSE, TRUE, NOW(), NOW(), 'user3', 'user3_mail', 1),
  (4, TRUE, FALSE, TRUE, NOW(), NOW(), 'user4', 'user4_mail', 1),
  (5, TRUE, FALSE, TRUE, NOW(), NOW(), 'user5', 'user5_mail', 1),
  (6, TRUE, FALSE, TRUE, NOW(), NOW(), 'user5', 'user6_mail', 1)
ON CONFLICT DO NOTHING;

INSERT
INTO
  tracks
  (id, name, about, start_date, end_date, type, min_constraint, max_constraint, max_second_course_constraint)
VALUES
  (1, 'first track', 'bo boo', '2024-09-01', '2025-05-05', 'bachelor', 3, 7, 3)
ON CONFLICT DO NOTHING;

INSERT
INTO
  students
  (id, has_team, is_captain, course, group_number, about_self, contacts, current_team_id, user_id)
VALUES
  (1, FALSE, FALSE, 1, 1, 'good', 'vk', NULL, 2),
  (2, TRUE, TRUE, 1, 1, 'bad', 'ok', NULL, 3),
  (3, TRUE, FALSE, 1, 1, 'simple', '88800', NULL, 4),
  (4, TRUE, TRUE, 1, 1, 'dimple', 'tg', NULL, 5),
  (5, FALSE, FALSE, 1, 1, 'pop-it', 'teams', NULL, 6)
ON CONFLICT DO NOTHING;

INSERT
INTO
  teams
  (id, captain_id, created_at, updated_at, name, project_description, project_type, quantity_of_students, is_full, current_track_id)
VALUES
  (1, 2, NOW(), NOW(), 'team1', 'project game', 'game', 1, FALSE, 1),
  (2, 4, NOW(), NOW(), 'team2', 'project web', 'web', 2, FALSE, 1)
ON CONFLICT DO NOTHING;

INSERT
INTO
  teams_students
  (team_id, student_id)
VALUES
  (1, 2),
  (2, 3),
  (2, 4)
ON CONFLICT DO NOTHING;

UPDATE
  students
SET
  current_team_id = 1
WHERE id = 2;
UPDATE
  students
SET
  current_team_id = 2
WHERE id = 3;
UPDATE
  students
SET
  current_team_id = 2
WHERE id = 4;