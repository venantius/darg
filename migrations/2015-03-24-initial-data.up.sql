-- 2015-03-24
-- Initial data migration

CREATE SCHEMA darg;

CREATE TABLE darg.user (
    id          SERIAL      PRIMARY KEY,
    email       text        UNIQUE,
    password    text        NOT NULL,
    name        text        NOT NULL,
    timezone    text        DEFAULT 'UTC',
    email_hour  text        DEFAULT '7PM',
    admin       boolean     DEFAULT FALSE NOT NULL,
    bot         boolean     DEFAULT FALSE NOT NULL,
    active      boolean     DEFAULT FALSE NOT NULL
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

CREATE TABLE darg.role (
    id          SERIAL      PRIMARY KEY,
    admin       boolean     DEFAULT FALSE,
    user_id     integer     NOT NULL references darg.user(id) ON DELETE CASCADE,
    team_id     integer     NOT NULL references darg.team(id) ON DELETE CASCADE
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
