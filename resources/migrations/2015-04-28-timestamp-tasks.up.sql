-- 2015-04-28
-- Make tasks use timestamps, not date

ALTER TABLE darg.task ALTER COLUMN date SET DATA TYPE timestamp with time zone;
ALTER TABLE darg.task ALTER COLUMN date SET DEFAULT now();
ALTER TABLE darg.task RENAME COLUMN date TO timestamp;
