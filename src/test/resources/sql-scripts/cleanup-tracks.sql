-- Clean up in correct order to handle foreign key constraints
--DELETE FROM track_student WHERE track_id IN (SELECT id FROM track);
DELETE FROM tracks WHERE id >= 1000;