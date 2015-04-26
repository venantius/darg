-- 2015-04-24
-- Email confirmations

CREATE TABLE darg.email_confirmation (
    id      SERIAL      PRIMARY KEY,
    user_id INTEGER     NOT NULL references darg.user(id) ON DELETE CASCADE,
    token   TEXT        UNIQUE NOT NULL
);

ALTER TABLE darg.user ADD COLUMN confirmed_email boolean DEFAULT false;
