INSERT
INTO
  users
  (id, role_id, email, fio, is_enabled, is_locked, is_remind_enabled, created_at, updated_at)
VALUES
    (1, 3, 'admin_mail', 'admin', true, false, true, '2024-11-20 18:28:15.047117', '2024-11-20 18:28:15.047117'),
    (7, 1, 'blue_sky47@example.com', 'Смирнов Алексей Петрович', false, false, true, '2024-11-21 20:24:36.402366', '2024-11-21 20:24:36.402366'),
    (19, 1, 'joyful_butterfly11@example.com', 'Крылов Денис Васильевич', true, false, true, '2024-11-21 20:24:36.402366', '2024-11-21 20:24:36.402366'),
    (18, 1, 'mystical_owl22@example.com', 'Цветкова Анастасия Николаевна', true, false, true, '2024-11-21 20:24:36.402366', '2024-11-21 20:24:36.402366'),
    (17, 1, 'playful_unicorn54@example.com', 'Лавров Роман Сергеевич', true, false, true, '2024-11-21 20:24:36.402366', '2024-11-21 20:24:36.402366'),
    (16, 1, 'glittering_seashell12@example.com', 'Морозова Ксения Владимировна', true, false, true, '2024-11-21 20:24:36.402366', '2024-11-21 20:24:36.402366'),
    (15, 1, 'echoing_voices29@example.com', 'Назаров Валентин Сергеевич', true, false, true, '2024-11-21 20:24:36.402366', '2024-11-21 20:24:36.402366'),
    (14, 1, 'vibrant_chameleon84@example.com', 'Соловьева Дарья Олеговна', true, false, true, '2024-11-21 20:24:36.402366', '2024-11-21 20:24:36.402366'),
    (13, 1, 'magical_rainbow65@example.com', 'Шевченко Максим Валерьевич', true, false, true, '2024-11-21 20:24:36.402366', '2024-11-21 20:24:36.402366'),
    (12, 1, 'radiant_firefly08@example.com', 'Нестерова Виктория Андреевна', true, false, true, '2024-11-21 20:24:36.402366', '2024-11-21 20:24:36.402366'),
    (11, 1, 'curious_squirrel17@example.com', 'Громов Павел Александрович', true, false, true, '2024-11-21 20:24:36.402366', '2024-11-21 20:24:36.402366'),
    (10, 1, 'serene_lake83@example.com', 'Алексеенко Светлана Сергеевна', true, false, true, '2024-11-21 20:24:36.402366', '2024-11-21 20:24:36.402366'),
    (9, 1, 'enchanted_forest30@example.com', 'Романов Илья Дмитриевич', true, false, true, '2024-11-21 20:24:36.402366', '2024-11-21 20:24:36.402366'),
    (8, 1, 'dancing_fox11@example.com', 'Кокшарова Ольга Евгеньевна', true, false, true, '2024-11-21 20:24:36.402366', '2024-11-21 20:24:36.402366'),
    (2, 1, 'user2_mail', 'Сергеева Мария Ивановна', true, false, true, '2024-11-20 18:28:15.047117', '2024-11-20 18:28:15.047117'),
    (3, 1, 'user3_mail', 'Кузнецов Дмитрий Александрович', true, false, true, '2024-11-20 18:28:15.047117', '2024-11-20 18:28:15.047117'),
    (4, 1, 'user4_mail', 'Лебедева Анна Викторовна', true, false, true, '2024-11-20 18:28:15.047117', '2024-11-20 18:28:15.047117'),
    (5, 1, 'user5_mail', 'Сидоров Сергей Михайлович', true, false, true, '2024-11-20 18:28:15.047117', '2024-11-20 18:28:15.047117'),
    (6, 1, 'user6_mail', 'Волкова Екатерина Юрьевна', true, false, true, '2024-11-20 18:28:15.047117', '2024-11-20 18:28:15.047117'),
    (20, 1, 'user20_mail', 'Мелихов Петр Андреевич', true, false, false, '2024-12-20 18:28:15.047117', '2024-12-20 18:28:15.047117'),
    (21, 1, 'user21_mail', 'Мелихов Павел Ога', true, false, false, '2024-12-20 18:28:15.047117', '2024-12-20 18:28:15.047117')
ON CONFLICT DO NOTHING;
SELECT pg_catalog.setval('users_id_seq', 19, true);

INSERT
INTO
  tracks
  (id, name, about, start_date, max_constraint, min_constraint,  max_second_course_constraint, end_date, type)
VALUES
    (1, 'first track', 'Участники этого трека работают над проектами, которые включают в себя анализ данных, разработку моделей обучения и внедрение их в реальную практику, что позволяет решать сложные и актуальные проблемы. ', '2025-05-05', 7, 3, 3, '2024-09-01', 'bachelor'),
    (2, 'second track', 'В этом треке студенты погружаются в мир веб-разработки. Участники создают собственные проекты, начиная от простых сайтов до сложных веб-приложений.', '2024-05-05', 7, 3, 3, '2023-09-01', 'bachelor'),
    (3, 'first track', 'Студенты этого трека применяют навыки программирования для создания мобильных платформ. Каждая команда реализует свой проект, ориентированный на решение реальных задач и потребностей пользователей.', '2023-05-05', 7, 3, 3, '2022-09-01', 'master')

ON CONFLICT DO NOTHING;
SELECT pg_catalog.setval('tracks_id_seq', 3, true);

INSERT
INTO
  students
  (id, course, group_number, is_captain, has_team, about_self, contacts, current_team_id, user_id)
VALUES
    (1, 1, 1, false, false, 'Изучаю веб-разработку и интересуюсь UX/UI дизайно', 'whatsapp', NULL, 2),
    (2, 1, 1, true, true, 'Фанат Python и люблю автоматизировать рутинные задачи', 'ok', 1, 3),
    (3, 1, 1, true, true, 'Занимаюсь кибербезопасностью и стремлюсь защитить данные пользователей', 'tg', 2, 4),
    (4, 1, 1, false, true, 'Изучаю машинное обучение и мечтаю работать в AI-стартапе', 'tg', 2, 5),
    (5, 1, 1, false, false, 'Люблю исследовать базы данных и оптимизировать их производительность', 'teams', NULL, 6),
    (6, 2, 1, false, false, 'Увлекаюсь разработкой мобильных приложений и люблю создавать что-то новое', 'vk', NULL, 7),
    (7, 1, 1, false, false, 'Изучаю системы управления контентом и создаю собственные сайты', 'whatsapp', NULL, 20),
    (8, 5, 1, false, true, 'Увлекаюсь интернет-маркетингом и знаю, как продвигать проекты онлайн', 'teams', 3, 10),
    (9, 1, 1, false, false, 'Программист и люблю работать с открытым кодом', 'vk', NULL, 8),
    (10, 1, 1, true, true, 'Изучаю DevOps и хочу улучшить процессы разработки и развертывания', 'ok', 4, 9),
    (11, 2, 1, false, false, 'Мечтаю создавать видеоигры', 'vk', NULL, 14),
    (12, 2, 1, false, true, 'Изучаю компьютерную графику и работаю над проектами анимации', 'tg', 1, 13),
    (13, 2, 1, false, false, 'Увлекаюсь блокчейн-технологиями и криптовалютами', 'tg', NULL, 12),
    (14, 5, 1, false, true, 'Люблю разрабатывать чат-ботов и создавать умные решения', 'tg', 3, 11),
    (15, 5, 1, false, true, 'Программирую на Java и интересуюсь разработкой корпоративных приложений', 'tg', 3, 16),
    (16, 5, 1, false, true, 'Изучаю философию технологий и их влияние на общество', 'tg', 3, 15),
    (17, 5, 1, true, true, 'Изучаю кроссплатформенную разработку и создаю приложения для всех устройств', 'teams', 3, 19),
    (18, 5, 1, false, false, 'Исследую машинное зрение и работаю с проектами на основе нейросетей', 'ok', NULL, 17),
    (19, 2, 1, false, true, 'Увлекаюсь тестированием ПО и стремлюсь к качеству в каждом проекте', 'ok', 4, 18)
ON CONFLICT DO NOTHING;
SELECT pg_catalog.setval('students_id_seq', 19, true);

INSERT
INTO
  teams
  (id, captain_id, is_full, name, project_description, project_type, quantity_of_students, current_track_id, created_at, updated_at)
VALUES
    (1, 2, false, 'Tech Titans', 'Мы создаем захватывающую многопользовательскую игру в жанре фэнтези.', 'game', 2, 1, '2024-11-20 18:28:15.047117', '2024-11-20 18:28:15.047117'),
    (2, 3, false, 'Byte Busters', 'Наша команда разрабатывает мобильное приложение для отслеживания здоровья и фитнеса', 'Mobile application', 2, 2, '2024-11-20 18:28:15.047117', '2024-11-20 18:28:15.047117'),
    (3, 17, false, 'Innovators United', 'Мы разрабатываем платформу для онлайн-обучения, которая адаптируется под нужды пользователя.', 'Cross-platform application', 4, 3, '2024-11-21 20:24:36.402366', '2024-11-21 20:24:36.402366'),
    (4, 10, false, 'Digital Nomads', 'Мы разрабатываем мобильное приложение, которое помогает пользователям эффективно управлять своими личными финансами, отслеживать расходы и доходы.', 'Mobile application', 2, 2, '2024-11-21 20:24:36.402366', '2024-11-21 20:24:36.402366')
ON CONFLICT DO NOTHING;
SELECT pg_catalog.setval('teams_id_seq', 4, true);

INSERT
INTO
  teams_students
  (team_id, student_id)
VALUES
    (1, 2),
    (1, 12),
    (2, 3),
    (2, 4),
    (3, 8),
    (3, 14),
    (3, 15),
    (3, 17),
    (4, 19),
    (4, 10),
    (4, 19)
ON CONFLICT DO NOTHING;
