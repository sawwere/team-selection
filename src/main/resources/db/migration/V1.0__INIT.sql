CREATE TABLE "students" (
  "id" bigint PRIMARY KEY NOT NULL,
  "course" integer,
  "group_number" integer,
  "is_captain" boolean,
  "has_team" boolean,
  "about_self" character varying(1024),
  "contacts" character varying(255),
  "current_team_id" bigint,
  "user_id" bigint UNIQUE
);

CREATE TABLE "teams" (
  "id" bigint PRIMARY KEY NOT NULL,
  "captain_id" bigint NOT NULL,
  "is_full" boolean,
  "name" character varying(255),
  "project_description" character varying(1024),
  "project_type" character varying(255),
  "quantity_of_students" integer,
  "current_track_id" bigint,
  "created_at" timestamp without time zone NOT NULL,
  "updated_at" timestamp without time zone NOT NULL
);

CREATE TABLE "tracks" (
  "id" bigint PRIMARY KEY NOT NULL,
  "about" character varying(255),
  "end_date" date,
  "max_constraint" integer,
  "max_second_course_constraint" integer,
  "min_constraint" integer,
  "name" character varying(255),
  "start_date" date,
  "type" character varying(255)
);

CREATE TABLE "users" (
  "id" bigint PRIMARY KEY NOT NULL,
  "role_id" bigint NOT NULL,
  "email" character varying(255),
  "fio" character varying(255),
  "is_enabled" boolean NOT NULL DEFAULT false,
  "is_locked" boolean NOT NULL DEFAULT false,
  "is_remind_enabled" boolean NOT NULL DEFAULT true,
  "created_at" timestamp without time zone NOT NULL,
  "updated_at" timestamp without time zone NOT NULL
);

CREATE TABLE "roles" (
  "id" bigint PRIMARY KEY NOT NULL,
  "name" character varying(255) UNIQUE NOT NULL
);

CREATE TABLE "technologies" (
  "id" bigint PRIMARY KEY NOT NULL,
  "name" character varying(255) UNIQUE NOT NULL
);

CREATE TABLE "applications" (
  "id" bigint PRIMARY KEY NOT NULL,
  "student_id" bigint NOT NULL,
  "team_id" bigint NOT NULL,
  "status" character varying(255) UNIQUE NOT NULL
);

CREATE TABLE "students_technologies" (
  "student_id" bigint,
  "technology_id" bigint,
  PRIMARY KEY ("student_id", "technology_id")
);

ALTER TABLE "students_technologies" ADD FOREIGN KEY ("student_id") REFERENCES "students" ("id");

ALTER TABLE "students_technologies" ADD FOREIGN KEY ("technology_id") REFERENCES "technologies" ("id");


CREATE TABLE "teams_technologies" (
  "team_id" bigint,
  "technology_id" bigint,
  PRIMARY KEY ("team_id", "technology_id")
);

ALTER TABLE "teams_technologies" ADD FOREIGN KEY ("team_id") REFERENCES "teams" ("id");

ALTER TABLE "teams_technologies" ADD FOREIGN KEY ("technology_id") REFERENCES "technologies" ("id");


ALTER TABLE "students" ADD CONSTRAINT "users_students" FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "roles" ADD CONSTRAINT "users_roles" FOREIGN KEY ("id") REFERENCES "users" ("id");

ALTER TABLE "teams" ADD CONSTRAINT "teams_tracks" FOREIGN KEY ("current_track_id") REFERENCES "tracks" ("id");

ALTER TABLE "teams" ADD CONSTRAINT "teams_captain" FOREIGN KEY ("captain_id") REFERENCES "teams" ("id");

ALTER TABLE "students" ADD CONSTRAINT "team_members" FOREIGN KEY ("current_team_id") REFERENCES "teams" ("id");

CREATE TABLE "teams_students" (
  "team_id" bigint,
  "student_id" bigint,
  PRIMARY KEY ("team_id", "student_id")
);

ALTER TABLE "teams_students" ADD FOREIGN KEY ("team_id") REFERENCES "teams" ("id");

ALTER TABLE "teams_students" ADD FOREIGN KEY ("student_id") REFERENCES "students" ("id");


ALTER TABLE "applications" ADD CONSTRAINT "teams_applications" FOREIGN KEY ("team_id") REFERENCES "teams" ("id");

ALTER TABLE "applications" ADD CONSTRAINT "students_applications" FOREIGN KEY ("student_id") REFERENCES "students" ("id");
