DROP TABLE github.team_repo;
DROP TABLE github.repo;
DROP TABLE github.team_settings;
DROP TABLE github.oauth_state;
DROP TABLE github.access_token;

CREATE TABLE github.token (
    id          SERIAL  PRIMARY KEY,
    gh_token    TEXT    NOT NULL,
    repo_scope  BOOLEAN DEFAULT FALSE
);

CREATE TABLE github.user (
    id              SERIAL  PRIMARY KEY,
    darg_user_id    INTEGER REFERENCES darg.user(id) ON DELETE CASCADE,
    github_token_id INTEGER REFERENCES github.token(id) ON DELETE SET NULL,
    gh_login        TEXT    ,
    gh_email        TEXT    ,
    gh_avatar_url   TEXT
);

CREATE TABLE github.repo (
    id              SERIAL      PRIMARY KEY,
    name            TEXT        NOT NULL,
    description     TEXT        ,
    html_url        TEXT        NOT NULL
);

CREATE TABLE github.push (
    id                  SERIAL          PRIMARY KEY,
    size                INTEGER         NOT NULL, -- number of commits in push
    ref                 TEXT            NOT NULL, -- full git ref (repo+branch)
    head_commit_message TEXT            NOT NULL, -- message on top commit
    compare_url         TEXT            NOT NULL,
    timestamp           TIMESTAMP       NOT NULL,
    github_user_id      INTEGER         REFERENCES github.user(id) ON DELETE SET NULL,
    github_repo_id      INTEGER         NOT NULL REFERENCES github.repo(id) ON DELETE CASCADE
);

CREATE TABLE github.issue (
    id              SERIAL          PRIMARY KEY,
    github_user_id  INTEGER         REFERENCES github.user(id) ON DELETE SET NULL,
    github_repo_id  INTEGER         NOT NULL REFERENCES github.repo(id) ON DELETE CASCADE,
    action          TEXT            NOT NULL,
    number          INTEGER         NOT NULL,
    title           TEXT            NOT NULL,
    url             TEXT            NOT NULL,
    timestamp       TIMESTAMP       NOT NULL
);

CREATE TABLE github.pull_request (
    id              SERIAL          PRIMARY KEY,
    github_user_id  INTEGER         REFERENCES github.user(id) ON DELETE SET NULL,
    github_repo_id  INTEGER         NOT NULL REFERENCES github.repo(id) ON DELETE CASCADE,
    action          TEXT            NOT NULL,
    number          INTEGER         NOT NULL,
    title           TEXT            NOT NULL,
    url             TEXT            NOT NULL,
    timestamp       TIMESTAMP       NOT NULL
);
