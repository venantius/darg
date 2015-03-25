-- 2015-03-24
-- Initial data migration (down)

DROP TABLE darg.password_reset_token;
DROP TABLE darg.api_key;
DROP TABLE github.pull_request;
DROP TABLE github.issue;
DROP TABLE github.push;
DROP TABLE darg.team_repo;
DROP TABLE darg.team_user;
DROP TABLE github.repo;
DROP TABLE darg.task;
DROP TABLE darg.team;
DROP TABLE darg.user;
DROP TABLE github.user;
DROP TABLE github.token;
DROP SCHEMA github;
DROP SCHEMA darg;
