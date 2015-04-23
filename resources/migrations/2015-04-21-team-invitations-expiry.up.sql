-- 2015-04-21
-- Alter table, add expires_at

ALTER TABLE darg.team_invitation ADD COLUMN expires_at timestamp DEFAULT now();
ALTER TABLE darg.team_invitation DROP COLUMN user_id;
