-- 2015-05-01
-- Various user options

ALTER TABLE darg.user ADD COLUMN created_at timestamp DEFAULT now();
ALTER TABLE darg.user ADD COLUMN send_daily_email boolean DEFAULT true;
ALTER TABLE darg.user ADD COLUMN send_digest_email boolean DEFAULT true;
