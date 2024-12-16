-- V2__create_recipient_table.sql

CREATE TABLE recipient (
                           id SERIAL PRIMARY KEY,
                           email_id INTEGER NOT NULL,
                           email VARCHAR(255) NOT NULL,
                           recipient_type VARCHAR(10) NOT NULL,
                           FOREIGN KEY (email_id) REFERENCES email(email_id) ON DELETE CASCADE
);