-- V4__add_subject_column.sql

ALTER TABLE email ADD COLUMN subject VARCHAR(255) NOT NULL DEFAULT 'No Subject';