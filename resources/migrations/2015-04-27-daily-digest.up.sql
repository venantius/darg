-- 2014-04-27
-- Add daily digest emails

ALTER TABLE darg.user ADD COLUMN digest_hour TEXT DEFAULT '9pm';
