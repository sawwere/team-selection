--USERS
INSERT INTO users (id, role_id, email, fio, is_enabled, is_locked, is_remind_enabled, created_at, updated_at) VALUES (27, 4, 'user27_mail', 'Орлова Екатерина Михайловна', true, false, true, '2024-11-20 18:28:15.047117', '2024-11-20 18:28:15.047117');
INSERT INTO users (id, role_id, email, fio, is_enabled, is_locked, is_remind_enabled, created_at, updated_at) VALUES (26, 4, 'user26_mail', 'Соколов Андрей Павлович', true, false, true, '2024-11-20 18:28:15.047117', '2024-11-20 18:28:15.047117');
INSERT INTO users (id, role_id, email, fio, is_enabled, is_locked, is_remind_enabled, created_at, updated_at) VALUES (25, 4, 'user25_mail', 'Кузнецова Мария Ивановна', true, false, true, '2024-11-20 18:28:15.047117', '2024-11-20 18:28:15.047117');
INSERT INTO users (id, role_id, email, fio, is_enabled, is_locked, is_remind_enabled, created_at, updated_at) VALUES (24, 4, 'user24_mail', 'Петров Дмитрий Николаевич', true, false, true, '2024-11-20 18:28:15.047117', '2024-11-20 18:28:15.047117');
INSERT INTO users (id, role_id, email, fio, is_enabled, is_locked, is_remind_enabled, created_at, updated_at) VALUES (23, 4, 'user23_mail', 'Васильева Анна Сергеевна', true, false, true, '2024-11-20 18:28:15.047117', '2024-11-20 18:28:15.047117');
INSERT INTO users (id, role_id, email, fio, is_enabled, is_locked, is_remind_enabled, created_at, updated_at) VALUES (22, 4, 'user22_mail', 'Смирнов Алексей Владимирович', true, false, true, '2024-11-20 18:28:15.047117', '2024-11-20 18:28:15.047117');


INSERT INTO students (id, course, group_number, is_captain, has_team, about_self, contacts, current_team_id, user_id) VALUES (20, 1, 1, true, true, ' Опытный разработчик с глубокими знаниями в области Java и Python', 'vk', null, 22);
--TEAMS
INSERT INTO teams (id, captain_id, is_full, name, project_description, quantity_of_students, current_track_id, created_at, updated_at, project_type_id) VALUES (5, 20, false, 'Matrix Minds', 'Наша команда создаст интеллектуальную систему мониторинга и аналитики, которая поможет владельцам онлайн-магазинов принимать обоснованные решения на основе данных.', 6, 1, '2024-11-20 18:28:15.047117', '2024-11-20 18:28:15.047117', 1);

--STUDENTS
INSERT INTO students (id, course, group_number, is_captain, has_team, about_self, contacts, current_team_id, user_id) VALUES (25, 2, 1, false, true, 'Product Manager с опытом ведения проектов от идеи до запуска', 'tg', 5, 27);
INSERT INTO students (id, course, group_number, is_captain, has_team, about_self, contacts, current_team_id, user_id) VALUES (24, 2, 1, false, true, 'Data Scientist с опытом анализа больших объемов данных и построения моделей машинного обучения', 'tg', 5, 26);
INSERT INTO students (id, course, group_number, is_captain, has_team, about_self, contacts, current_team_id, user_id) VALUES (23, 1, 1, false, true, 'Системный администратор с опытом настройки и поддержки Linux-серверов', 'tg', 5, 25);
INSERT INTO students (id, course, group_number, is_captain, has_team, about_self, contacts, current_team_id, user_id) VALUES (22, 1, 1, false, true, 'Full Stack Developer с опытом разработки веб-приложений на React.js и Node.js', 'ok', 5, 24);
INSERT INTO students (id, course, group_number, is_captain, has_team, about_self, contacts, current_team_id, user_id) VALUES (21, 1, 1, false, true, 'Владею навыками написания тестов на Python и JavaScript, настройки окружения для тестирования, работы с баг-трекинговыми системами (Jira, Trello)', 'vk', 5, 23);

UPDATE students
SET current_team_id = 5
WHERE id = 20;

--TEAMS_STUDENTS
INSERT INTO teams_students (team_id, student_id) VALUES (5, 20);
INSERT INTO teams_students (team_id, student_id) VALUES (5, 21);
INSERT INTO teams_students (team_id, student_id) VALUES (5, 22);
INSERT INTO teams_students (team_id, student_id) VALUES (5, 23);
INSERT INTO teams_students (team_id, student_id) VALUES (5, 24);
INSERT INTO teams_students (team_id, student_id) VALUES (5, 25);
