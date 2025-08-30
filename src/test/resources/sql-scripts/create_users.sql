INSERT INTO users VALUES (101, 1, 'user26@_mail', 'Васильева Екатерина Петровна', true, false, true, '2024-11-20 18:28:15.047117', '2024-11-20 18:28:15.047117');
INSERT INTO users VALUES (102, 1, 'user25@_mail', 'Иванов Дмитрий Александрович', true, false, true, '2024-11-20 18:28:15.047117', '2024-11-20 18:28:15.047117');
INSERT INTO users VALUES (103, 2, 'user_jury24@_mail', 'Смирнова Анна Владимировна', true, false, true, '2024-11-20 18:28:15.047117', '2024-11-20 18:28:15.047117');
INSERT INTO users VALUES (104, 2, 'user_jury23@_mail', 'Кузнецов Иван Сергеевич', true, false, true, '2024-11-20 18:28:15.047117', '2024-11-20 18:28:15.047117');
INSERT INTO users VALUES (105, 4, 'user_104@_mail', 'Куз Нец Ван', true, false, true, '2024-11-20 18:28:15.047117', '2024-11-20 18:28:15.047117');

INSERT INTO students (id, course, group_number, is_captain, has_team, about_self, contacts, current_team_id, user_id) VALUES (105, 1, 1, true, true, ' Опытный разработчик', 'vk', null, 105);
INSERT INTO students_technologies (student_id, technology_id) VALUES (105, 1);