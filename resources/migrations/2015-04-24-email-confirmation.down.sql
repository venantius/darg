-- 2015-04-24
-- Remove email confirmations

ALTER TABLE darg.user DROP COLUMN confirmed_email;
DROP TABLE darg.email_confirmation;
