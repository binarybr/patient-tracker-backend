-- V3__users_hardening.sql
-- Purpose: Harden users table with audit and security fields.
-- Note: last_login_at and password_changed_at add traceability; locked boolean supports account lockouts.
ALTER TABLE users ADD COLUMN last_login_at TIMESTAMP NULL;

ALTER TABLE users ADD COLUMN password_changed_at TIMESTAMP NULL;

ALTER TABLE users ADD COLUMN locked BOOLEAN NOT NULL DEFAULT FALSE;
