-- 2015-05-19
-- Tasks submitted by email

ALTER TABLE darg.task ALTER COLUMN timestamp DROP NOT NULL;
ALTER TABLE darg.task ALTER COLUMN timestamp DROP DEFAULT;
ALTER TABLE darg.task ADD COLUMN date DATE;
ALTER TABLE darg.task ADD COLUMN type TEXT;
