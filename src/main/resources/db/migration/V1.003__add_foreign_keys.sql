
ALTER TABLE "students_technologies" ADD FOREIGN KEY ("student_id") REFERENCES "students" ("id");

ALTER TABLE "students_technologies" ADD FOREIGN KEY ("technology_id") REFERENCES "technologies" ("id");

ALTER TABLE "teams_technologies" ADD FOREIGN KEY ("team_id") REFERENCES "teams" ("id");

ALTER TABLE "teams_technologies" ADD FOREIGN KEY ("technology_id") REFERENCES "technologies" ("id");


ALTER TABLE "students" ADD CONSTRAINT "users_students" FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "users" ADD CONSTRAINT "users_roles" FOREIGN KEY ("role_id") REFERENCES "roles" ("id");

ALTER TABLE "teams" ADD CONSTRAINT "teams_tracks" FOREIGN KEY ("current_track_id") REFERENCES "tracks" ("id");

ALTER TABLE "teams" ADD CONSTRAINT "teams_captain" FOREIGN KEY ("captain_id") REFERENCES "students" ("id");

ALTER TABLE "students" ADD CONSTRAINT "team_members" FOREIGN KEY ("current_team_id") REFERENCES "teams" ("id");

ALTER TABLE "teams_students" ADD FOREIGN KEY ("team_id") REFERENCES "teams" ("id");

ALTER TABLE "teams_students" ADD FOREIGN KEY ("student_id") REFERENCES "students" ("id");


ALTER TABLE "applications" ADD CONSTRAINT "teams_applications" FOREIGN KEY ("team_id") REFERENCES "teams" ("id");

ALTER TABLE "applications" ADD CONSTRAINT "students_applications" FOREIGN KEY ("student_id") REFERENCES "students" ("id");
