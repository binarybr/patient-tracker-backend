-- V7__password_reset_tokens.sql
-- Purpose: Create password_reset_tokens table for one-time reset flows (hash-only storage).
-- Note: Indexes support lookup by email and expiry pruning.
CREATE TABLE password_reset_tokens (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 email VARCHAR(255) NOT NULL,
 token_hash VARCHAR(255) NOT NULL,
 expires_at TIMESTAMP NOT NULL,
 used BOOLEAN NOT NULL DEFAULT FALSE,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX ix_pwdreset_email ON password_reset_tokens(email);

CREATE INDEX ix_pwdreset_expires ON password_reset_tokens(expires_at);
