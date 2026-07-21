/* UC-01/04/05/06 + reservation/table/payment hardening (SQL Server, rerunnable). */

IF OBJECT_ID('password_reset_tokens', 'U') IS NULL
BEGIN
    CREATE TABLE password_reset_tokens (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        email NVARCHAR(255) NOT NULL,
        token_hash VARCHAR(64) NOT NULL,
        created_at DATETIME2 NOT NULL,
        expires_at DATETIME2 NOT NULL,
        failed_attempts INT NOT NULL CONSTRAINT df_password_reset_attempts DEFAULT 0,
        used_at DATETIME2 NULL
    );
    CREATE INDEX ix_password_reset_email_created ON password_reset_tokens(email, created_at DESC);
END;

IF COL_LENGTH('reservations', 'assigned_table_id') IS NULL ALTER TABLE reservations ADD assigned_table_id BIGINT NULL;
IF COL_LENGTH('reservations', 'start_at') IS NULL ALTER TABLE reservations ADD start_at DATETIME2 NULL;
IF COL_LENGTH('reservations', 'end_at') IS NULL ALTER TABLE reservations ADD end_at DATETIME2 NULL;
IF COL_LENGTH('reservations', 'created_order_id') IS NULL ALTER TABLE reservations ADD created_order_id BIGINT NULL;

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ux_reservations_created_order_id')
    CREATE UNIQUE FILTERED INDEX ux_reservations_created_order_id ON reservations(created_order_id)
    WHERE created_order_id IS NOT NULL;

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ix_reservations_table_slot')
    CREATE INDEX ix_reservations_table_slot ON reservations(assigned_table_id, start_at, end_at, status);

IF COL_LENGTH('restaurant_tables', 'original_capacity') IS NULL
BEGIN
    ALTER TABLE restaurant_tables ADD original_capacity INT NULL;
    UPDATE restaurant_tables SET original_capacity = capacity WHERE original_capacity IS NULL;
    ALTER TABLE restaurant_tables ALTER COLUMN original_capacity INT NOT NULL;
END;

IF COL_LENGTH('restaurant_tables', 'version') IS NULL
BEGIN
    ALTER TABLE restaurant_tables ADD version BIGINT NULL;
    UPDATE restaurant_tables SET version = 0 WHERE version IS NULL;
    ALTER TABLE restaurant_tables ALTER COLUMN version BIGINT NOT NULL;
END;
