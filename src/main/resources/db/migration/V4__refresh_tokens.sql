-- V4__refresh_tokens.sql
-- Purpose: Introduce refresh_tokens table to store hashed tokens for rotation and revocation.
-- Note: Indexes on subject and expires_at optimize validation & cleanup jobs.
-- Note: replaced_by stores hash of successor token.
CREATE TABLE refresh_tokens (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 subject VARCHAR(255) NOT NULL,
 token_hash VARCHAR(255) NOT NULL,
 expires_at TIMESTAMP NOT NULL,
 revoked BOOLEAN NOT NULL DEFAULT FALSE,
 replaced_by VARCHAR(255) NULL,
 user_agent VARCHAR(255) NULL,
 ip_address VARCHAR(64) NULL,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX ix_refresh_subject ON refresh_tokens(subject);

CREATE INDEX ix_refresh_expires ON refresh_tokens(expires_at);
