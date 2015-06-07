-- 2015-05-26
-- Adjust github schema to have some clearer names

DROP SCHEMA github CASCADE;
CREATE SCHEMA github;

CREATE TABLE github.access_token (
    id              SERIAL      PRIMARY KEY,
    darg_user_id    INTEGER     REFERENCES darg.user(id) ON DELETE CASCADE,
    token           TEXT        NOT NULL,
    scope           TEXT
);

CREATE TABLE github.oauth_state (
    id              SERIAL      PRIMARY KEY,
    darg_user_id    INTEGER     REFERENCES darg.user(id) ON DELETE CASCADE,
    darg_team_id    INTEGER     REFERENCES darg.team(id) ON DELETE CASCADE,
    state           TEXT        NOT NULL
);

CREATE TABLE github.team_settings (
    id                  SERIAL  PRIMARY KEY,
    darg_team_id        INTEGER UNIQUE REFERENCES darg.team(id) ON DELETE CASCADE,
    access_token_id     INTEGER REFERENCES github.access_token(id) ON DELETE SET NULL,
    commits             BOOLEAN NOT NULL DEFAULT TRUE,
    commit_comments     BOOLEAN NOT NULL DEFAULT TRUE,
    pull_requests       BOOLEAN NOT NULL DEFAULT TRUE,
    issues              BOOLEAN NOT NULL DEFAULT TRUE,
    pr_issue_comments   BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE github.repo (
    id          SERIAL  PRIMARY KEY,
    name        TEXT    NOT NULL,
    full_name   TEXT    NOT NULL
);

CREATE TABLE github.team_repo (
    id              SERIAL      PRIMARY KEY,
    darg_team_id    INTEGER     REFERENCES darg.team(id) ON DELETE CASCADE,
    repo_id         INTEGER     REFERENCES github.repo(id) ON DELETE CASCADE
);

CREATE TABLE github.user (
    id              SERIAL  PRIMARY KEY,
    darg_user_id    INTEGER UNIQUE NOT NULL REFERENCES darg.user(id) ON DELETE CASCADE,
    access_token_id INTEGER REFERENCES github.access_token(id) ON DELETE SET NULL,
    login           TEXT    NOT NULL
);

