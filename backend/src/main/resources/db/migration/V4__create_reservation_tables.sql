-- V4: Reservation Tables (Moved up from V5 to resolve foreign key dependency in Orders)
CREATE TABLE IF NOT EXISTS restaurant_tables (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    table_number VARCHAR(20) NOT NULL,
    capacity INT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',
    location VARCHAR(100),
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    created_by BIGINT,
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    updated_by BIGINT,
    deleted_at DATETIME(6),
    deleted_by BIGINT,
    CONSTRAINT uk_table_number UNIQUE (table_number),
    CONSTRAINT chk_table_capacity CHECK (capacity > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    table_id BIGINT NOT NULL,
    reservation_time DATETIME(6) NOT NULL,
    guest_count INT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    special_request TEXT,
    version BIGINT NOT NULL DEFAULT 0,
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    created_by BIGINT,
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    updated_by BIGINT,
    deleted_at DATETIME(6),
    deleted_by BIGINT,
    CONSTRAINT chk_reservation_guests CHECK (guest_count > 0),
    CONSTRAINT fk_reservation_customer FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_reservation_table FOREIGN KEY (table_id) REFERENCES restaurant_tables(id) ON DELETE RESTRICT,
    INDEX idx_reservation_time (reservation_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
