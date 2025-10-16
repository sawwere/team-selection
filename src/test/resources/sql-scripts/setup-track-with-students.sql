INSERT INTO tracks (id, name, about, start_date, end_date, type, min_constraint, max_constraint, max_second_course_constraint)
VALUES (1000, 'Track With Students', 'Has students', '2024-02-01', '2024-04-01', 'bachelor', 5, 20, 10);

-- Assuming you have a student table and track_student relation
INSERT INTO students (id, current_track_id, about_self, contacts, course, group_number) VALUES (2001, 1000, 'Test Student', 'student@test.com', 2, 2);