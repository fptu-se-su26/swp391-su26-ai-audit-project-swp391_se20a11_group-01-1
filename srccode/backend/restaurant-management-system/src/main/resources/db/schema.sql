-- ============================================================
-- Restaurant Management System - Database Schema
-- ============================================================

CREATE DATABASE IF NOT EXISTS restaurant_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE restaurant_db;

-- ============================================================
-- PERSON 1
-- ============================================================

CREATE TABLE IF NOT EXISTS roles (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    full_name  VARCHAR(100),
    phone      VARCHAR(15),
    address    VARCHAR(255),
    avatar_url VARCHAR(255),
    status     VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS categories (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS foods (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(150)   NOT NULL,
    description TEXT,
    price       DECIMAL(10, 2) NOT NULL,
    image_url   VARCHAR(255),
    status      VARCHAR(20)    NOT NULL DEFAULT 'AVAILABLE',
    category_id BIGINT,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_food_category FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS reviews (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    rating     INT         NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment    TEXT,
    user_id    BIGINT      NOT NULL,
    food_id    BIGINT      NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_review_food FOREIGN KEY (food_id) REFERENCES foods (id) ON DELETE CASCADE
);

-- ============================================================
-- PERSON 2
-- ============================================================

CREATE TABLE IF NOT EXISTS restaurant_tables (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    number   INT         NOT NULL UNIQUE,
    capacity INT         NOT NULL,
    status   VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE'
);

CREATE TABLE IF NOT EXISTS coupons (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    code            VARCHAR(50)    NOT NULL UNIQUE,
    discount_type   VARCHAR(20)    NOT NULL,
    discount_value  DECIMAL(10, 2) NOT NULL,
    min_order_value DECIMAL(10, 2) DEFAULT 0,
    max_uses        INT            DEFAULT NULL,
    used_count      INT            DEFAULT 0,
    expired_at      DATETIME       DEFAULT NULL,
    is_active       BOOLEAN        DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS orders (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT         NOT NULL,
    total_price  DECIMAL(10, 2) NOT NULL,
    status       VARCHAR(20)    NOT NULL DEFAULT 'PENDING',
    coupon_id    BIGINT         DEFAULT NULL,
    note         TEXT,
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_user   FOREIGN KEY (user_id)   REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_order_coupon FOREIGN KEY (coupon_id) REFERENCES coupons (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS order_details (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id   BIGINT         NOT NULL,
    food_id    BIGINT         NOT NULL,
    quantity   INT            NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_od_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT fk_od_food  FOREIGN KEY (food_id)  REFERENCES foods (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS carts (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT NOT NULL UNIQUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS cart_items (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id  BIGINT NOT NULL,
    food_id  BIGINT NOT NULL,
    quantity INT    NOT NULL DEFAULT 1,
    CONSTRAINT fk_ci_cart FOREIGN KEY (cart_id) REFERENCES carts (id) ON DELETE CASCADE,
    CONSTRAINT fk_ci_food FOREIGN KEY (food_id) REFERENCES foods (id) ON DELETE CASCADE,
    UNIQUE(cart_id, food_id)
);

CREATE TABLE IF NOT EXISTS reservations (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT      NOT NULL,
    table_id         BIGINT      NOT NULL,
    reservation_time DATETIME    NOT NULL,
    guest_count      INT         NOT NULL,
    status           VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    note             TEXT,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_res_user  FOREIGN KEY (user_id)  REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_res_table FOREIGN KEY (table_id) REFERENCES restaurant_tables (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS payments (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id       BIGINT         NOT NULL UNIQUE,
    amount         DECIMAL(10, 2) NOT NULL,
    method         VARCHAR(30)    NOT NULL,
    status         VARCHAR(20)    NOT NULL DEFAULT 'PENDING',
    transaction_id VARCHAR(100)   DEFAULT NULL,
    paid_at        DATETIME       DEFAULT NULL,
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
);

-- ============================================================
-- SEED DATA
-- ============================================================

INSERT IGNORE INTO roles (name) VALUES ('ROLE_ADMIN'), ('ROLE_STAFF'), ('ROLE_CUSTOMER');
