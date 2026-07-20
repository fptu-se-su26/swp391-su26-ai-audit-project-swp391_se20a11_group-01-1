-- UC-21 / UC-22 safe SQL Server migration.
-- This script is idempotent and preserves rows from the legacy vouchers schema.
SET XACT_ABORT ON;
BEGIN TRANSACTION;

IF OBJECT_ID(N'dbo.vouchers', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.vouchers (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        code VARCHAR(50) NOT NULL,
        name NVARCHAR(150) NOT NULL,
        description NVARCHAR(500) NULL,
        discount_type VARCHAR(20) NOT NULL,
        discount_value DECIMAL(12,2) NOT NULL,
        max_discount_amount DECIMAL(12,2) NULL,
        min_order_amount DECIMAL(12,2) NOT NULL,
        start_at DATETIME2 NOT NULL,
        end_at DATETIME2 NOT NULL,
        usage_limit INT NOT NULL,
        usage_limit_per_user INT NOT NULL,
        used_count INT NOT NULL CONSTRAINT DF_vouchers_used_count DEFAULT 0,
        active BIT NOT NULL CONSTRAINT DF_vouchers_active DEFAULT 1,
        version BIGINT NOT NULL CONSTRAINT DF_vouchers_version DEFAULT 0,
        created_at DATETIME2 NOT NULL CONSTRAINT DF_vouchers_created_at DEFAULT SYSUTCDATETIME(),
        updated_at DATETIME2 NULL
    );
END;
GO

IF COL_LENGTH('dbo.vouchers', 'name') IS NULL ALTER TABLE dbo.vouchers ADD name NVARCHAR(150) NULL;
    IF COL_LENGTH('dbo.vouchers', 'description') IS NULL ALTER TABLE dbo.vouchers ADD description NVARCHAR(500) NULL;
    IF COL_LENGTH('dbo.vouchers', 'discount_type') IS NULL ALTER TABLE dbo.vouchers ADD discount_type VARCHAR(20) NULL;
    IF COL_LENGTH('dbo.vouchers', 'discount_value') IS NULL ALTER TABLE dbo.vouchers ADD discount_value DECIMAL(12,2) NULL;
    IF COL_LENGTH('dbo.vouchers', 'max_discount_amount') IS NULL ALTER TABLE dbo.vouchers ADD max_discount_amount DECIMAL(12,2) NULL;
    IF COL_LENGTH('dbo.vouchers', 'min_order_amount') IS NULL ALTER TABLE dbo.vouchers ADD min_order_amount DECIMAL(12,2) NULL;
    IF COL_LENGTH('dbo.vouchers', 'start_at') IS NULL ALTER TABLE dbo.vouchers ADD start_at DATETIME2 NULL;
    IF COL_LENGTH('dbo.vouchers', 'end_at') IS NULL ALTER TABLE dbo.vouchers ADD end_at DATETIME2 NULL;
    IF COL_LENGTH('dbo.vouchers', 'usage_limit') IS NULL ALTER TABLE dbo.vouchers ADD usage_limit INT NULL;
    IF COL_LENGTH('dbo.vouchers', 'usage_limit_per_user') IS NULL ALTER TABLE dbo.vouchers ADD usage_limit_per_user INT NULL;
    IF COL_LENGTH('dbo.vouchers', 'used_count') IS NULL ALTER TABLE dbo.vouchers ADD used_count INT NULL;
IF COL_LENGTH('dbo.vouchers', 'version') IS NULL ALTER TABLE dbo.vouchers ADD version BIGINT NULL;
GO

UPDATE dbo.vouchers SET name = code WHERE name IS NULL OR LTRIM(RTRIM(name)) = '';

    IF COL_LENGTH('dbo.vouchers', 'type') IS NOT NULL
        EXEC(N'UPDATE dbo.vouchers SET discount_type = UPPER([type]) WHERE discount_type IS NULL');
    UPDATE dbo.vouchers SET discount_type = 'FIXED' WHERE discount_type IS NULL OR discount_type NOT IN ('PERCENT', 'FIXED');

    IF COL_LENGTH('dbo.vouchers', 'discount') IS NOT NULL
        EXEC(N'UPDATE dbo.vouchers SET discount_value = [discount] WHERE discount_value IS NULL');
    UPDATE dbo.vouchers SET discount_value = 0.01 WHERE discount_value IS NULL OR discount_value <= 0;

    IF COL_LENGTH('dbo.vouchers', 'min_order') IS NOT NULL
        EXEC(N'UPDATE dbo.vouchers SET min_order_amount = min_order WHERE min_order_amount IS NULL');
    UPDATE dbo.vouchers SET min_order_amount = 0 WHERE min_order_amount IS NULL OR min_order_amount < 0;

    IF COL_LENGTH('dbo.vouchers', 'expiry') IS NOT NULL
        EXEC(N'UPDATE dbo.vouchers SET end_at = DATEADD(DAY, 1, CAST(expiry AS DATETIME2)) WHERE end_at IS NULL');
    UPDATE dbo.vouchers SET start_at = COALESCE(created_at, SYSUTCDATETIME()) WHERE start_at IS NULL;
    UPDATE dbo.vouchers SET end_at = DATEADD(YEAR, 1, start_at) WHERE end_at IS NULL OR end_at <= start_at;

    IF COL_LENGTH('dbo.vouchers', 'total') IS NOT NULL
        EXEC(N'UPDATE dbo.vouchers SET usage_limit = [total] WHERE usage_limit IS NULL');
    UPDATE dbo.vouchers SET usage_limit = 1 WHERE usage_limit IS NULL OR usage_limit < 1;

    IF COL_LENGTH('dbo.vouchers', 'used') IS NOT NULL
        EXEC(N'UPDATE dbo.vouchers SET used_count = [used] WHERE used_count IS NULL');
    UPDATE dbo.vouchers SET used_count = 0 WHERE used_count IS NULL OR used_count < 0;
    UPDATE dbo.vouchers SET usage_limit = used_count WHERE used_count > usage_limit;
    UPDATE dbo.vouchers SET usage_limit_per_user = 1 WHERE usage_limit_per_user IS NULL OR usage_limit_per_user < 1;
    UPDATE dbo.vouchers SET version = 0 WHERE version IS NULL;

    ALTER TABLE dbo.vouchers ALTER COLUMN name NVARCHAR(150) NOT NULL;
    ALTER TABLE dbo.vouchers ALTER COLUMN discount_type VARCHAR(20) NOT NULL;
    ALTER TABLE dbo.vouchers ALTER COLUMN discount_value DECIMAL(12,2) NOT NULL;
    ALTER TABLE dbo.vouchers ALTER COLUMN min_order_amount DECIMAL(12,2) NOT NULL;
    ALTER TABLE dbo.vouchers ALTER COLUMN start_at DATETIME2 NOT NULL;
    ALTER TABLE dbo.vouchers ALTER COLUMN end_at DATETIME2 NOT NULL;
    ALTER TABLE dbo.vouchers ALTER COLUMN usage_limit INT NOT NULL;
    ALTER TABLE dbo.vouchers ALTER COLUMN usage_limit_per_user INT NOT NULL;
    ALTER TABLE dbo.vouchers ALTER COLUMN used_count INT NOT NULL;
ALTER TABLE dbo.vouchers ALTER COLUMN version BIGINT NOT NULL;
GO

-- Keep legacy columns for rollback/read compatibility, but stop requiring new writes to populate them.
IF COL_LENGTH('dbo.vouchers', 'discount') IS NOT NULL EXEC(N'ALTER TABLE dbo.vouchers ALTER COLUMN discount NUMERIC(12,2) NULL');
IF COL_LENGTH('dbo.vouchers', 'expiry') IS NOT NULL EXEC(N'ALTER TABLE dbo.vouchers ALTER COLUMN expiry DATE NULL');
IF COL_LENGTH('dbo.vouchers', 'min_order') IS NOT NULL EXEC(N'ALTER TABLE dbo.vouchers ALTER COLUMN min_order NUMERIC(12,2) NULL');
IF COL_LENGTH('dbo.vouchers', 'total') IS NOT NULL EXEC(N'ALTER TABLE dbo.vouchers ALTER COLUMN total INT NULL');
IF COL_LENGTH('dbo.vouchers', 'type') IS NOT NULL EXEC(N'ALTER TABLE dbo.vouchers ALTER COLUMN [type] VARCHAR(20) NULL');
IF COL_LENGTH('dbo.vouchers', 'used') IS NOT NULL EXEC(N'ALTER TABLE dbo.vouchers ALTER COLUMN used INT NULL');
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE object_id = OBJECT_ID('dbo.vouchers') AND name = 'UX_vouchers_code')
    CREATE UNIQUE INDEX UX_vouchers_code ON dbo.vouchers(code);

IF OBJECT_ID(N'dbo.voucher_usages', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.voucher_usages (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        voucher_id BIGINT NOT NULL,
        user_id BIGINT NULL,
        order_id BIGINT NULL,
        reservation_id BIGINT NULL,
        discount_amount DECIMAL(12,2) NOT NULL,
        status VARCHAR(20) NOT NULL,
        used_at DATETIME2 NOT NULL CONSTRAINT DF_voucher_usages_used_at DEFAULT SYSUTCDATETIME(),
        reversed_at DATETIME2 NULL,
        created_at DATETIME2 NOT NULL CONSTRAINT DF_voucher_usages_created_at DEFAULT SYSUTCDATETIME(),
        CONSTRAINT FK_voucher_usages_voucher FOREIGN KEY (voucher_id) REFERENCES dbo.vouchers(id),
        CONSTRAINT FK_voucher_usages_user FOREIGN KEY (user_id) REFERENCES dbo.users(user_id),
        CONSTRAINT FK_voucher_usages_order FOREIGN KEY (order_id) REFERENCES dbo.orders(order_id),
        CONSTRAINT FK_voucher_usages_reservation FOREIGN KEY (reservation_id) REFERENCES dbo.reservations(reservation_id),
        CONSTRAINT CK_voucher_usages_reference CHECK ((order_id IS NOT NULL AND reservation_id IS NULL) OR (order_id IS NULL AND reservation_id IS NOT NULL)),
        CONSTRAINT CK_voucher_usages_status CHECK (status IN ('APPLIED', 'REVERSED')),
        CONSTRAINT UQ_voucher_usages_voucher_order UNIQUE (voucher_id, order_id),
        CONSTRAINT UQ_voucher_usages_voucher_reservation UNIQUE (voucher_id, reservation_id)
    );
    CREATE INDEX IX_voucher_usages_voucher_user_status ON dbo.voucher_usages(voucher_id, user_id, status);
END;

IF COL_LENGTH('dbo.orders', 'voucher_code') IS NULL ALTER TABLE dbo.orders ADD voucher_code NVARCHAR(50) NULL;
IF COL_LENGTH('dbo.orders', 'voucher_discount_amount') IS NULL ALTER TABLE dbo.orders ADD voucher_discount_amount DECIMAL(12,2) NULL;
IF COL_LENGTH('dbo.reservations', 'voucher_code') IS NULL ALTER TABLE dbo.reservations ADD voucher_code NVARCHAR(50) NULL;
IF COL_LENGTH('dbo.reservations', 'voucher_discount_amount') IS NULL ALTER TABLE dbo.reservations ADD voucher_discount_amount DECIMAL(12,2) NULL;

COMMIT TRANSACTION;
