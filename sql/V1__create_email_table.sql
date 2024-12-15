-- V1__create_email_table.sql

CREATE TYPE email_status AS ENUM ('DRAFT', 'SENT', 'DELETED', 'SPAM');

CREATE TABLE email (
                       email_id SERIAL PRIMARY KEY,
                       email_from VARCHAR(255) NOT NULL,
                       email_body TEXT,
                       state email_status,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);