-- 2015-03-24
-- Initial data migration

CREATE SCHEMA darg;
CREATE SCHEMA github;

CREATE TABLE github.token (
    id SERIAL PRIMARY KEY,
    gh_token text NOT NULL,
    repo_scope boolean DEFAULT FALSE
);

CREATE TABLE github.user (
    id              SERIAL  PRIMARY KEY,
    gh_login        text    ,
    gh_email        text    ,
    gh_avatar_url   text    ,
    github_token_id integer references github.token(id) ON DELETE SET NULL
);

CREATE TABLE darg.user (
    id          SERIAL      PRIMARY KEY,
    email       text        UNIQUE,
    password    text        NOT NULL,
    name        text        NOT NULL,
    timezone    text        DEFAULT 'UTC',
    email_hour  text        DEFAULT '7PM',
    admin       boolean     DEFAULT FALSE NOT NULL,
    bot         boolean     DEFAULT FALSE NOT NULL,
    active      boolean     DEFAULT FALSE NOT NULL,
    github_user_id integer references github.user(id) ON DELETE SET NULL
);

CREATE TABLE darg.team (
    id          SERIAL      PRIMARY KEY,
    name        text        NOT NULL,
    email       text        UNIQUE NOT NULL
);

CREATE TABLE darg.task (
    id          SERIAL      PRIMARY KEY,
    date        date        NOT NULL,
    task        text,
    user_id     integer     NOT NULL references darg.user(id) ON DELETE SET NULL,
    team_id     integer     NOT NULL references darg.team(id) ON DELETE CASCADE
);

CREATE TABLE github.repo (
    id              SERIAL      PRIMARY KEY,
    name            text        NOT NULL,
    description     text        ,
    html_url        text        NOT NULL
);

CREATE TABLE darg.team_user (
    id          SERIAL      PRIMARY KEY,
    admin       boolean     DEFAULT FALSE,
    user_id     integer     NOT NULL references darg.user(id) ON DELETE CASCADE,
    team_id     integer     NOT NULL references darg.team(id) ON DELETE CASCADE
);

CREATE TABLE darg.team_repo (
    id              SERIAL      PRIMARY KEY,
    active          boolean     DEFAULT 'false',
    github_repo_id  integer     NOT NULL references github.repo(id) ON DELETE CASCADE,
    team_id         integer     references darg.team(id) ON DELETE CASCADE
);

CREATE TABLE github.push (
    id              SERIAL          PRIMARY KEY,
    size            integer         NOT NULL, -- number of commits in push
    ref             text            NOT NULL, -- full git ref (repo+branch)
    head_commit_message text        NOT NULL, -- message on top commit
    compare_url     text            NOT NULL,
    timestamp       timestamp       NOT NULL,
    github_user_id  integer         references github.user(id) ON DELETE SET NULL,
    github_repo_id  integer         NOT NULL references github.repo(id) ON DELETE CASCADE
);

CREATE TABLE github.issue (
    id          SERIAL          PRIMARY KEY,
    gh_user_id  integer         REFERENCES github.user(id) ON DELETE SET NULL,
    gh_repo_id  integer         NOT NULL REFERENCES github.repo(id) ON DELETE CASCADE,
    action      text            NOT NULL,
    number      integer         NOT NULL,
    title       text            NOT NULL,
    url         text            NOT NULL,
    timestamp   timestamp       NOT NULL
);

CREATE TABLE github.pull_request (
    id          SERIAL          PRIMARY KEY,
    gh_user_id  integer         REFERENCES github.user(id) ON DELETE SET NULL,
    gh_repo_id  integer         NOT NULL REFERENCES github.repo(id) ON DELETE CASCADE,
    action      text            NOT NULL,
    number      integer         NOT NULL,
    title       text            NOT NULL,
    url         text            NOT NULL,
    timestamp   timestamp       NOT NULL
);

CREATE TABLE darg.api_key (
    id      SERIAL          PRIMARY KEY,
    api_key text            UNIQUE NOT NULL,
    user_id integer         NOT NULL REFERENCES darg.user(id) ON DELETE CASCADE
);

CREATE TABLE darg.password_reset_token (
    id      SERIAL          PRIMARY KEY,
    token   text            UNIQUE NOT NULL,
    user_id integer         NOT NULL REFERENCES darg.user(id) ON DELETE CASCADE,
    expires_at  timestamp   NOT NULL DEFAULT now()
);
