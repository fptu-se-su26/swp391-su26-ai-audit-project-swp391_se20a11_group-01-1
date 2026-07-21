SET XACT_ABORT ON;
BEGIN TRANSACTION;

IF COL_LENGTH('users', 'full_name') IS NULL ALTER TABLE users ADD full_name NVARCHAR(150) NULL;
IF COL_LENGTH('users', 'phone') IS NULL ALTER TABLE users ADD phone VARCHAR(30) NULL;
IF COL_LENGTH('users', 'avatar_url') IS NULL ALTER TABLE users ADD avatar_url VARCHAR(500) NULL;
IF COL_LENGTH('users', 'updated_at') IS NULL ALTER TABLE users ADD updated_at DATETIME2 NULL;
IF COL_LENGTH('users', 'token_version') IS NULL
BEGIN
    ALTER TABLE users ADD token_version INT NULL;
    EXEC('UPDATE users SET token_version = 0 WHERE token_version IS NULL');
    EXEC('ALTER TABLE users ALTER COLUMN token_version INT NOT NULL');
END;

IF OBJECT_ID('refresh_tokens', 'U') IS NULL
BEGIN
    CREATE TABLE refresh_tokens (
        refresh_token_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        user_id BIGINT NOT NULL,
        token_hash VARCHAR(64) NOT NULL,
        family_id VARCHAR(36) NOT NULL,
        expires_at DATETIME2 NOT NULL,
        created_at DATETIME2 NOT NULL,
        last_used_at DATETIME2 NULL,
        revoked_at DATETIME2 NULL,
        ip_address VARCHAR(45) NULL,
        user_agent VARCHAR(500) NULL,
        CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(user_id),
        CONSTRAINT uq_refresh_tokens_hash UNIQUE (token_hash)
    );
    CREATE INDEX ix_refresh_tokens_user ON refresh_tokens(user_id);
    CREATE INDEX ix_refresh_tokens_family ON refresh_tokens(family_id);
END;

IF OBJECT_ID('security_audit_logs', 'U') IS NULL
BEGIN
    CREATE TABLE security_audit_logs (
        audit_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        actor_user_id BIGINT NULL,
        target_user_id BIGINT NULL,
        action VARCHAR(80) NOT NULL,
        old_value VARCHAR(500) NULL,
        new_value VARCHAR(500) NULL,
        ip_address VARCHAR(45) NULL,
        created_at DATETIME2 NOT NULL
    );
    CREATE INDEX ix_security_audit_actor ON security_audit_logs(actor_user_id);
    CREATE INDEX ix_security_audit_target ON security_audit_logs(target_user_id);
END;

COMMIT TRANSACTION;
