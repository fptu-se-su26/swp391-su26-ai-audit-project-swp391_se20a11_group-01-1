/*
    SQL Server manual migration for item-level kitchen processing.

    This project does not currently include Flyway or Liquibase, so this file is
    NOT executed automatically. Back up the database and run it manually before
    deploying the application changes.

    The new columns intentionally remain nullable during the compatibility phase.
*/

SET XACT_ABORT ON;

BEGIN TRY
    BEGIN TRANSACTION;

    IF COL_LENGTH(N'dbo.order_items', N'status') IS NULL
    BEGIN
        ALTER TABLE dbo.order_items ADD [status] VARCHAR(30) NULL;
    END;

    IF COL_LENGTH(N'dbo.order_items', N'note') IS NULL
    BEGIN
        ALTER TABLE dbo.order_items ADD [note] NVARCHAR(500) NULL;
    END;

    IF COL_LENGTH(N'dbo.order_items', N'created_at') IS NULL
    BEGIN
        ALTER TABLE dbo.order_items ADD created_at DATETIME2 NULL;
    END;

    IF COL_LENGTH(N'dbo.order_items', N'status_updated_at') IS NULL
    BEGIN
        ALTER TABLE dbo.order_items ADD status_updated_at DATETIME2 NULL;
    END;

    UPDATE item
    SET item.[status] = CASE parent.[status]
        WHEN 'PENDING' THEN 'CONFIRMED'
        WHEN 'CONFIRMED' THEN 'CONFIRMED'
        WHEN 'PREPARING' THEN 'PREPARING'
        WHEN 'READY' THEN 'READY'
        WHEN 'COMPLETED' THEN 'READY'
        WHEN 'CANCELLED' THEN 'CANCELLED'
        ELSE 'CONFIRMED'
    END
    FROM dbo.order_items AS item
    INNER JOIN dbo.orders AS parent ON parent.order_id = item.order_id
    WHERE item.[status] IS NULL;

    UPDATE item
    SET item.created_at = COALESCE(parent.created_at, SYSDATETIME())
    FROM dbo.order_items AS item
    INNER JOIN dbo.orders AS parent ON parent.order_id = item.order_id
    WHERE item.created_at IS NULL;

    UPDATE item
    SET item.status_updated_at = COALESCE(
        parent.updated_at,
        parent.created_at,
        SYSDATETIME()
    )
    FROM dbo.order_items AS item
    INNER JOIN dbo.orders AS parent ON parent.order_id = item.order_id
    WHERE item.status_updated_at IS NULL;

    UPDATE item
    SET item.[note] = parent.[note]
    FROM dbo.order_items AS item
    INNER JOIN dbo.orders AS parent ON parent.order_id = item.order_id
    WHERE item.[note] IS NULL
      AND parent.[note] IS NOT NULL;

    /* Synchronize the persisted Order.status summary with the backfilled items. */
    UPDATE parent
    SET parent.[status] = CASE
        WHEN EXISTS (
            SELECT 1
            FROM dbo.payments AS payment
            WHERE payment.order_id = parent.order_id
              AND payment.[status] = 'PAID'
        ) THEN 'COMPLETED'
        WHEN NOT EXISTS (
            SELECT 1
            FROM dbo.order_items AS active_item
            WHERE active_item.order_id = parent.order_id
              AND active_item.[status] <> 'CANCELLED'
        ) THEN 'CANCELLED'
        WHEN NOT EXISTS (
            SELECT 1
            FROM dbo.order_items AS non_ready_item
            WHERE non_ready_item.order_id = parent.order_id
              AND non_ready_item.[status] <> 'CANCELLED'
              AND non_ready_item.[status] <> 'READY'
        ) THEN 'READY'
        WHEN NOT EXISTS (
            SELECT 1
            FROM dbo.order_items AS non_confirmed_item
            WHERE non_confirmed_item.order_id = parent.order_id
              AND non_confirmed_item.[status] <> 'CANCELLED'
              AND non_confirmed_item.[status] <> 'CONFIRMED'
        ) THEN 'CONFIRMED'
        ELSE 'PREPARING'
    END
    FROM dbo.orders AS parent
    WHERE EXISTS (
        SELECT 1
        FROM dbo.order_items AS any_item
        WHERE any_item.order_id = parent.order_id
    );

    IF NOT EXISTS (
        SELECT 1
        FROM sys.default_constraints AS default_constraint
        INNER JOIN sys.columns AS column_info
            ON column_info.default_object_id = default_constraint.object_id
        INNER JOIN sys.tables AS table_info
            ON table_info.object_id = column_info.object_id
        WHERE table_info.name = N'order_items'
          AND SCHEMA_NAME(table_info.schema_id) = N'dbo'
          AND column_info.name = N'status'
    )
    BEGIN
        ALTER TABLE dbo.order_items
            ADD CONSTRAINT DF_order_items_status
            DEFAULT ('CONFIRMED') FOR [status];
    END;

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF XACT_STATE() <> 0
    BEGIN
        ROLLBACK TRANSACTION;
    END;

    THROW;
END CATCH;

/* All counts must be zero before considering a later NOT NULL migration. */
SELECT
    COALESCE(SUM(CASE WHEN [status] IS NULL THEN 1 ELSE 0 END), 0)
        AS null_status_count,
    COALESCE(SUM(CASE WHEN created_at IS NULL THEN 1 ELSE 0 END), 0)
        AS null_created_at_count,
    COALESCE(SUM(CASE WHEN status_updated_at IS NULL THEN 1 ELSE 0 END), 0)
        AS null_status_updated_at_count,
    COALESCE(SUM(CASE
        WHEN [status] NOT IN ('CONFIRMED', 'PREPARING', 'READY', 'CANCELLED')
            THEN 1
        ELSE 0
    END), 0) AS invalid_status_count
FROM dbo.order_items;

/*
    Later hardening phase (run separately only after deployment verification):

    ALTER TABLE dbo.order_items
        ALTER COLUMN [status] VARCHAR(30) NOT NULL;

    The current Java entity intentionally does not request NOT NULL yet.
*/
