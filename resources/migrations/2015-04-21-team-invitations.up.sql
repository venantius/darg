-- 2015-04-21
-- Adding a table for team invitations

CREATE TABLE darg.team_invitation (
    id      SERIAL      PRIMARY KEY,
    user_id INTEGER     NOT NULL references darg.user(id) ON DELETE CASCADE,
    team_id INTEGER     NOT NULL references darg.team(id) ON DELETE CASCADE,
    token   TEXT        UNIQUE NOT NULL
);
