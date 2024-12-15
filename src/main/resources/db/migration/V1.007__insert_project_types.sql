INSERT
INTO
  project_types
  (id, name)
VALUES
  (1, 'Web'),
  (2, 'Mobile'),
  (3, 'Desktop'),
  (4, 'CrossPlatform'),
  (5, 'Game'),
  (6, 'Bot'),
  (7, 'Mod'),
  (8, 'Other');

SELECT pg_catalog.setval('project_types_id_seq', 8, true);

