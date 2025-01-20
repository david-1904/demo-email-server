
-- Drop existing sequence if it exists
DROP SEQUENCE IF EXISTS email_email_id_seq CASCADE;

-- Create new sequence with correct increment size
CREATE SEQUENCE email_email_id_seq START 1 INCREMENT 5;

-- Set default for email_id to use the sequence
ALTER TABLE email ALTER COLUMN email_id SET DEFAULT nextval('email_email_id_seq');

-- Drop existing sequence if it exists
DROP SEQUENCE IF EXISTS recipient_id_seq CASCADE;

-- Create new sequence with correct increment size
CREATE SEQUENCE recipient_id_seq START 1 INCREMENT 5;

-- Adjust the id column in the recipient table
ALTER TABLE recipient ALTER COLUMN id TYPE bigint;

-- Set default for id to use the sequence
ALTER TABLE recipient ALTER COLUMN id SET DEFAULT nextval('recipient_id_seq');