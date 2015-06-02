-- 2015-05-26
-- Adjust github schema to have some clearer names

DROP SCHEMA github CASCADE;
CREATE SCHEMA github;

CREATE TABLE github.access_token (
    id              SERIAL      PRIMARY KEY,
    darg_user_id    INTEGER     REFERENCES darg.user(id) ON DELETE CASCADE,
    token           TEXT        NOT NULL,
    scope           TEXT
)
