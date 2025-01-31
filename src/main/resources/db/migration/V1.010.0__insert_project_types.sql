ALTER TABLE applications ADD type TEXT;

UPDATE applications
SET type = 'request';

ALTER TABLE applications ALTER COLUMN type SET NOT NULL;

