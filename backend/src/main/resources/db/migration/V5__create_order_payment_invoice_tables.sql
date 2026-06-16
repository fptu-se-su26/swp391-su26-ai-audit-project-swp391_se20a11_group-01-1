-- V5: Order, Payment, Invoice Tables (Moved from V4 to V5 to resolve dependency on Tables)
CREATE TABLE IF NOT EXISTS restaurant_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT,
    table_id BIGINT,
    coupon_id BIGINT,
    order_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    order_type VARCHAR(50) NOT NULL,
    sub_total DECIMAL(19,2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(19,2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(19,2) NOT NULL DEFAULT 0,
    note TEXT,
    version BIGINT NOT NULL DEFAULT 0,
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    created_by BIGINT,
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    updated_by BIGINT,
    CONSTRAINT chk_order_sub_total CHECK (sub_total >= 0),
    CONSTRAINT chk_order_total CHECK (total_amount >= 0),
    CONSTRAINT fk_order_customer FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_order_table FOREIGN KEY (table_id) REFERENCES restaurant_tables(id) ON DELETE RESTRICT,
    CONSTRAINT fk_order_coupon FOREIGN KEY (coupon_id) REFERENCES coupons(id) ON DELETE RESTRICT,
    INDEX idx_order_status (order_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    food_item_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(19,2) NOT NULL,
    note VARCHAR(255),
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    created_by BIGINT,
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    updated_by BIGINT,
    CONSTRAINT chk_order_item_qty CHECK (quantity > 0),
    CONSTRAINT chk_order_item_price CHECK (unit_price >= 0),
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES restaurant_orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_item_food FOREIGN KEY (food_item_id) REFERENCES food_items(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS coupon_usages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    coupon_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    used_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    created_by BIGINT,
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    updated_by BIGINT,
    CONSTRAINT uk_coupon_usage_order UNIQUE (order_id),
    CONSTRAINT fk_coupon_usage_coupon FOREIGN KEY (coupon_id) REFERENCES coupons(id) ON DELETE CASCADE,
    CONSTRAINT fk_coupon_usage_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_coupon_usage_order_ref FOREIGN KEY (order_id) REFERENCES restaurant_orders(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    amount DECIMAL(19,2) NOT NULL,
    transaction_code VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    created_by BIGINT,
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    updated_by BIGINT,
    CONSTRAINT chk_payment_amount CHECK (amount >= 0),
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES restaurant_orders(id) ON DELETE RESTRICT,
    INDEX idx_payment_status (payment_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS invoices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id BIGINT NOT NULL,
    invoice_number VARCHAR(100) NOT NULL,
    tax_amount DECIMAL(19,2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(19,2) NOT NULL,
    issued_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    created_by BIGINT,
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    updated_by BIGINT,
    CONSTRAINT uk_invoice_payment_id UNIQUE (payment_id),
    CONSTRAINT uk_invoice_number UNIQUE (invoice_number),
    CONSTRAINT chk_invoice_tax CHECK (tax_amount >= 0),
    CONSTRAINT chk_invoice_total CHECK (total_amount >= 0),
    CONSTRAINT fk_invoice_payment FOREIGN KEY (payment_id) REFERENCES payments(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
