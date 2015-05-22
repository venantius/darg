ALTER TABLE darg.task DROP COLUMN type;

-- Fill in timestamp values where they might be null
UPDATE darg.task SET timestamp = date WHERE timestamp IS NULL AND date IS NOT NULL;
UPDATE darg.task SET timestamp = now() WHERE timestamp IS NULL AND date IS NULL;
ALTER TABLE darg.task DROP COLUMN date;
ALTER TABLE darg.task ALTER COLUMN timestamp SET DEFAULT now();
ALTER TABLE darg.task ALTER COLUMN timestamp SET NOT NULL;
