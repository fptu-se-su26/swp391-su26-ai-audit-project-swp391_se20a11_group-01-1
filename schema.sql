/*
    schema.sql
    Restaurant Management System ERD
    Database: Microsoft SQL Server / SQL Server Management Studio

    Note:
    - This script creates all tables, primary keys, foreign keys, unique constraints,
      check constraints and useful indexes.
    - Table names use snake_case/plural style to avoid SQL Server reserved keywords.
      Example: ERD entity User  -> table users
               ERD entity Order -> table orders
*/

SET NOCOUNT ON;

/* =========================================================
   Drop tables in reverse dependency order
   ========================================================= */
DROP TABLE IF EXISTS dbo.food_recommendations;
DROP TABLE IF EXISTS dbo.ai_chat_messages;
DROP TABLE IF EXISTS dbo.ai_chat_sessions;
DROP TABLE IF EXISTS dbo.support_messages;
DROP TABLE IF EXISTS dbo.support_tickets;
DROP TABLE IF EXISTS dbo.reviews;
DROP TABLE IF EXISTS dbo.notifications;
DROP TABLE IF EXISTS dbo.reservations;
DROP TABLE IF EXISTS dbo.restaurant_tables;
DROP TABLE IF EXISTS dbo.kitchen_tasks;
DROP TABLE IF EXISTS dbo.invoices;
DROP TABLE IF EXISTS dbo.payments;
DROP TABLE IF EXISTS dbo.order_items;
DROP TABLE IF EXISTS dbo.orders;
DROP TABLE IF EXISTS dbo.cart_items;
DROP TABLE IF EXISTS dbo.carts;
DROP TABLE IF EXISTS dbo.coupons;
DROP TABLE IF EXISTS dbo.food_images;
DROP TABLE IF EXISTS dbo.foods;
DROP TABLE IF EXISTS dbo.categories;
DROP TABLE IF EXISTS dbo.role_permissions;
DROP TABLE IF EXISTS dbo.user_roles;
DROP TABLE IF EXISTS dbo.permissions;
DROP TABLE IF EXISTS dbo.roles;
DROP TABLE IF EXISTS dbo.users;

/* =========================================================
   1. Authentication & Authorization
   ========================================================= */
CREATE TABLE dbo.users (
    user_id BIGINT IDENTITY(1,1) NOT NULL,
    full_name NVARCHAR(100) NOT NULL,
    email NVARCHAR(255) NOT NULL,
    password_hash NVARCHAR(255) NOT NULL,
    phone NVARCHAR(20) NULL,
    address NVARCHAR(255) NULL,
    status NVARCHAR(20) NOT NULL CONSTRAINT df_users_status DEFAULT N'ACTIVE',
    created_at DATETIME2(0) NOT NULL CONSTRAINT df_users_created_at DEFAULT SYSDATETIME(),
    updated_at DATETIME2(0) NULL,

    CONSTRAINT pk_users PRIMARY KEY (user_id),
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT ck_users_status CHECK (status IN (N'ACTIVE', N'INACTIVE', N'LOCKED'))
);

CREATE TABLE dbo.roles (
    role_id BIGINT IDENTITY(1,1) NOT NULL,
    role_name NVARCHAR(50) NOT NULL,
    description NVARCHAR(255) NULL,

    CONSTRAINT pk_roles PRIMARY KEY (role_id),
    CONSTRAINT uq_roles_role_name UNIQUE (role_name)
);

CREATE TABLE dbo.permissions (
    permission_id BIGINT IDENTITY(1,1) NOT NULL,
    permission_name NVARCHAR(100) NOT NULL,
    description NVARCHAR(255) NULL,

    CONSTRAINT pk_permissions PRIMARY KEY (permission_id),
    CONSTRAINT uq_permissions_permission_name UNIQUE (permission_name)
);

CREATE TABLE dbo.user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    assigned_at DATETIME2(0) NOT NULL CONSTRAINT df_user_roles_assigned_at DEFAULT SYSDATETIME(),

    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES dbo.users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES dbo.roles(role_id) ON DELETE CASCADE
);

CREATE TABLE dbo.role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,

    CONSTRAINT pk_role_permissions PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES dbo.roles(role_id) ON DELETE CASCADE,
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES dbo.permissions(permission_id) ON DELETE CASCADE
);

/* =========================================================
   2. Menu & Promotion
   ========================================================= */
CREATE TABLE dbo.categories (
    category_id BIGINT IDENTITY(1,1) NOT NULL,
    category_name NVARCHAR(100) NOT NULL,
    description NVARCHAR(255) NULL,
    status NVARCHAR(20) NOT NULL CONSTRAINT df_categories_status DEFAULT N'ACTIVE',

    CONSTRAINT pk_categories PRIMARY KEY (category_id),
    CONSTRAINT uq_categories_category_name UNIQUE (category_name),
    CONSTRAINT ck_categories_status CHECK (status IN (N'ACTIVE', N'INACTIVE'))
);

CREATE TABLE dbo.foods (
    food_id BIGINT IDENTITY(1,1) NOT NULL,
    category_id BIGINT NOT NULL,
    food_name NVARCHAR(150) NOT NULL,
    description NVARCHAR(MAX) NULL,
    price DECIMAL(18,2) NOT NULL,
    availability_status NVARCHAR(20) NOT NULL CONSTRAINT df_foods_availability_status DEFAULT N'AVAILABLE',
    estimated_cooking_time INT NULL,
    created_at DATETIME2(0) NOT NULL CONSTRAINT df_foods_created_at DEFAULT SYSDATETIME(),
    updated_at DATETIME2(0) NULL,

    CONSTRAINT pk_foods PRIMARY KEY (food_id),
    CONSTRAINT fk_foods_category FOREIGN KEY (category_id) REFERENCES dbo.categories(category_id),
    CONSTRAINT ck_foods_price CHECK (price >= 0),
    CONSTRAINT ck_foods_estimated_cooking_time CHECK (estimated_cooking_time IS NULL OR estimated_cooking_time >= 0),
    CONSTRAINT ck_foods_availability_status CHECK (availability_status IN (N'AVAILABLE', N'UNAVAILABLE', N'SOLD_OUT'))
);

CREATE TABLE dbo.food_images (
    image_id BIGINT IDENTITY(1,1) NOT NULL,
    food_id BIGINT NOT NULL,
    image_url NVARCHAR(500) NOT NULL,
    is_primary BIT NOT NULL CONSTRAINT df_food_images_is_primary DEFAULT 0,
    uploaded_at DATETIME2(0) NOT NULL CONSTRAINT df_food_images_uploaded_at DEFAULT SYSDATETIME(),

    CONSTRAINT pk_food_images PRIMARY KEY (image_id),
    CONSTRAINT fk_food_images_food FOREIGN KEY (food_id) REFERENCES dbo.foods(food_id) ON DELETE CASCADE
);

CREATE TABLE dbo.coupons (
    coupon_id BIGINT IDENTITY(1,1) NOT NULL,
    code NVARCHAR(50) NOT NULL,
    discount_type NVARCHAR(20) NOT NULL,
    discount_value DECIMAL(18,2) NOT NULL,
    min_order_amount DECIMAL(18,2) NOT NULL CONSTRAINT df_coupons_min_order_amount DEFAULT 0,
    start_date DATETIME2(0) NOT NULL,
    end_date DATETIME2(0) NOT NULL,
    status NVARCHAR(20) NOT NULL CONSTRAINT df_coupons_status DEFAULT N'ACTIVE',

    CONSTRAINT pk_coupons PRIMARY KEY (coupon_id),
    CONSTRAINT uq_coupons_code UNIQUE (code),
    CONSTRAINT ck_coupons_discount_type CHECK (discount_type IN (N'PERCENTAGE', N'FIXED_AMOUNT')),
    CONSTRAINT ck_coupons_discount_value CHECK (discount_value > 0),
    CONSTRAINT ck_coupons_min_order_amount CHECK (min_order_amount >= 0),
    CONSTRAINT ck_coupons_date_range CHECK (end_date >= start_date),
    CONSTRAINT ck_coupons_status CHECK (status IN (N'ACTIVE', N'INACTIVE', N'EXPIRED'))
);

/* =========================================================
   3. Cart, Order, Payment & Kitchen
   ========================================================= */
CREATE TABLE dbo.carts (
    cart_id BIGINT IDENTITY(1,1) NOT NULL,
    customer_id BIGINT NOT NULL,
    cart_status NVARCHAR(20) NOT NULL CONSTRAINT df_carts_cart_status DEFAULT N'ACTIVE',
    created_at DATETIME2(0) NOT NULL CONSTRAINT df_carts_created_at DEFAULT SYSDATETIME(),
    updated_at DATETIME2(0) NULL,

    CONSTRAINT pk_carts PRIMARY KEY (cart_id),
    CONSTRAINT fk_carts_customer FOREIGN KEY (customer_id) REFERENCES dbo.users(user_id),
    CONSTRAINT ck_carts_cart_status CHECK (cart_status IN (N'ACTIVE', N'CHECKED_OUT', N'ABANDONED'))
);

CREATE TABLE dbo.cart_items (
    cart_item_id BIGINT IDENTITY(1,1) NOT NULL,
    cart_id BIGINT NOT NULL,
    food_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(18,2) NOT NULL,
    created_at DATETIME2(0) NOT NULL CONSTRAINT df_cart_items_created_at DEFAULT SYSDATETIME(),

    CONSTRAINT pk_cart_items PRIMARY KEY (cart_item_id),
    CONSTRAINT uq_cart_items_cart_food UNIQUE (cart_id, food_id),
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES dbo.carts(cart_id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_food FOREIGN KEY (food_id) REFERENCES dbo.foods(food_id),
    CONSTRAINT ck_cart_items_quantity CHECK (quantity > 0),
    CONSTRAINT ck_cart_items_unit_price CHECK (unit_price >= 0)
);

CREATE TABLE dbo.orders (
    order_id BIGINT IDENTITY(1,1) NOT NULL,
    customer_id BIGINT NOT NULL,
    staff_id BIGINT NULL,
    coupon_id BIGINT NULL,
    order_type NVARCHAR(20) NOT NULL CONSTRAINT df_orders_order_type DEFAULT N'DINE_IN',
    order_status NVARCHAR(30) NOT NULL CONSTRAINT df_orders_order_status DEFAULT N'PENDING',
    subtotal_amount DECIMAL(18,2) NOT NULL CONSTRAINT df_orders_subtotal_amount DEFAULT 0,
    discount_amount DECIMAL(18,2) NOT NULL CONSTRAINT df_orders_discount_amount DEFAULT 0,
    total_amount DECIMAL(18,2) NOT NULL CONSTRAINT df_orders_total_amount DEFAULT 0,
    note NVARCHAR(500) NULL,
    created_at DATETIME2(0) NOT NULL CONSTRAINT df_orders_created_at DEFAULT SYSDATETIME(),

    CONSTRAINT pk_orders PRIMARY KEY (order_id),
    CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES dbo.users(user_id),
    CONSTRAINT fk_orders_staff FOREIGN KEY (staff_id) REFERENCES dbo.users(user_id),
    CONSTRAINT fk_orders_coupon FOREIGN KEY (coupon_id) REFERENCES dbo.coupons(coupon_id),
    CONSTRAINT ck_orders_order_type CHECK (order_type IN (N'DINE_IN', N'TAKEAWAY', N'DELIVERY')),
    CONSTRAINT ck_orders_order_status CHECK (order_status IN (N'PENDING', N'PENDING_PAYMENT', N'CONFIRMED', N'PREPARING', N'READY', N'COMPLETED', N'CANCELLED')),
    CONSTRAINT ck_orders_subtotal_amount CHECK (subtotal_amount >= 0),
    CONSTRAINT ck_orders_discount_amount CHECK (discount_amount >= 0),
    CONSTRAINT ck_orders_total_amount CHECK (total_amount >= 0),
    CONSTRAINT ck_orders_total_logic CHECK (total_amount = subtotal_amount - discount_amount OR total_amount >= 0)
);

CREATE TABLE dbo.order_items (
    order_item_id BIGINT IDENTITY(1,1) NOT NULL,
    order_id BIGINT NOT NULL,
    food_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(18,2) NOT NULL,
    item_status NVARCHAR(30) NOT NULL CONSTRAINT df_order_items_item_status DEFAULT N'PENDING',
    kitchen_note NVARCHAR(500) NULL,

    CONSTRAINT pk_order_items PRIMARY KEY (order_item_id),
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES dbo.orders(order_id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_food FOREIGN KEY (food_id) REFERENCES dbo.foods(food_id),
    CONSTRAINT ck_order_items_quantity CHECK (quantity > 0),
    CONSTRAINT ck_order_items_unit_price CHECK (unit_price >= 0),
    CONSTRAINT ck_order_items_item_status CHECK (item_status IN (N'PENDING', N'PREPARING', N'READY', N'SERVED', N'CANCELLED', N'REJECTED'))
);

CREATE TABLE dbo.payments (
    payment_id BIGINT IDENTITY(1,1) NOT NULL,
    order_id BIGINT NOT NULL,
    payment_method NVARCHAR(30) NOT NULL,
    payment_status NVARCHAR(30) NOT NULL CONSTRAINT df_payments_payment_status DEFAULT N'PENDING',
    amount DECIMAL(18,2) NOT NULL,
    transaction_code NVARCHAR(100) NULL,
    paid_at DATETIME2(0) NULL,
    created_at DATETIME2(0) NOT NULL CONSTRAINT df_payments_created_at DEFAULT SYSDATETIME(),

    CONSTRAINT pk_payments PRIMARY KEY (payment_id),
    CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES dbo.orders(order_id),
    CONSTRAINT ck_payments_payment_method CHECK (payment_method IN (N'CASH', N'ONLINE', N'QR', N'CARD', N'BANK_TRANSFER')),
    CONSTRAINT ck_payments_payment_status CHECK (payment_status IN (N'UNPAID', N'PENDING', N'PAID', N'FAILED', N'REFUNDED')),
    CONSTRAINT ck_payments_amount CHECK (amount >= 0)
);

CREATE TABLE dbo.invoices (
    invoice_id BIGINT IDENTITY(1,1) NOT NULL,
    order_id BIGINT NOT NULL,
    invoice_number NVARCHAR(50) NOT NULL,
    total_amount DECIMAL(18,2) NOT NULL,
    issued_at DATETIME2(0) NOT NULL CONSTRAINT df_invoices_issued_at DEFAULT SYSDATETIME(),
    pdf_url NVARCHAR(500) NULL,

    CONSTRAINT pk_invoices PRIMARY KEY (invoice_id),
    CONSTRAINT uq_invoices_order_id UNIQUE (order_id),
    CONSTRAINT uq_invoices_invoice_number UNIQUE (invoice_number),
    CONSTRAINT fk_invoices_order FOREIGN KEY (order_id) REFERENCES dbo.orders(order_id),
    CONSTRAINT ck_invoices_total_amount CHECK (total_amount >= 0)
);

CREATE TABLE dbo.kitchen_tasks (
    kitchen_task_id BIGINT IDENTITY(1,1) NOT NULL,
    order_item_id BIGINT NOT NULL,
    kitchen_user_id BIGINT NOT NULL,
    task_status NVARCHAR(30) NOT NULL CONSTRAINT df_kitchen_tasks_task_status DEFAULT N'WAITING',
    started_at DATETIME2(0) NULL,
    finished_at DATETIME2(0) NULL,
    note NVARCHAR(500) NULL,

    CONSTRAINT pk_kitchen_tasks PRIMARY KEY (kitchen_task_id),
    CONSTRAINT fk_kitchen_tasks_order_item FOREIGN KEY (order_item_id) REFERENCES dbo.order_items(order_item_id),
    CONSTRAINT fk_kitchen_tasks_kitchen_user FOREIGN KEY (kitchen_user_id) REFERENCES dbo.users(user_id),
    CONSTRAINT ck_kitchen_tasks_task_status CHECK (task_status IN (N'WAITING', N'PREPARING', N'READY', N'REJECTED', N'COMPLETED')),
    CONSTRAINT ck_kitchen_tasks_time CHECK (finished_at IS NULL OR started_at IS NULL OR finished_at >= started_at)
);

/* =========================================================
   4. Table Reservation
   ========================================================= */
CREATE TABLE dbo.restaurant_tables (
    table_id BIGINT IDENTITY(1,1) NOT NULL,
    table_number NVARCHAR(20) NOT NULL,
    capacity INT NOT NULL,
    table_status NVARCHAR(30) NOT NULL CONSTRAINT df_restaurant_tables_table_status DEFAULT N'AVAILABLE',
    location NVARCHAR(100) NULL,

    CONSTRAINT pk_restaurant_tables PRIMARY KEY (table_id),
    CONSTRAINT uq_restaurant_tables_table_number UNIQUE (table_number),
    CONSTRAINT ck_restaurant_tables_capacity CHECK (capacity > 0),
    CONSTRAINT ck_restaurant_tables_table_status CHECK (table_status IN (N'AVAILABLE', N'RESERVED', N'OCCUPIED', N'OUT_OF_SERVICE'))
);

CREATE TABLE dbo.reservations (
    reservation_id BIGINT IDENTITY(1,1) NOT NULL,
    customer_id BIGINT NOT NULL,
    table_id BIGINT NOT NULL,
    approved_by BIGINT NULL,
    reservation_time DATETIME2(0) NOT NULL,
    guest_count INT NOT NULL,
    reservation_status NVARCHAR(30) NOT NULL CONSTRAINT df_reservations_reservation_status DEFAULT N'PENDING',
    note NVARCHAR(500) NULL,
    created_at DATETIME2(0) NOT NULL CONSTRAINT df_reservations_created_at DEFAULT SYSDATETIME(),

    CONSTRAINT pk_reservations PRIMARY KEY (reservation_id),
    CONSTRAINT fk_reservations_customer FOREIGN KEY (customer_id) REFERENCES dbo.users(user_id),
    CONSTRAINT fk_reservations_table FOREIGN KEY (table_id) REFERENCES dbo.restaurant_tables(table_id),
    CONSTRAINT fk_reservations_approved_by FOREIGN KEY (approved_by) REFERENCES dbo.users(user_id),
    CONSTRAINT ck_reservations_guest_count CHECK (guest_count > 0),
    CONSTRAINT ck_reservations_reservation_status CHECK (reservation_status IN (N'PENDING', N'APPROVED', N'REJECTED', N'CANCELLED', N'COMPLETED'))
);

/* =========================================================
   5. Review, Support & Notification
   ========================================================= */
CREATE TABLE dbo.notifications (
    notification_id BIGINT IDENTITY(1,1) NOT NULL,
    user_id BIGINT NOT NULL,
    notification_type NVARCHAR(20) NOT NULL,
    title NVARCHAR(150) NOT NULL,
    message NVARCHAR(MAX) NOT NULL,
    is_read BIT NOT NULL CONSTRAINT df_notifications_is_read DEFAULT 0,
    sent_at DATETIME2(0) NOT NULL CONSTRAINT df_notifications_sent_at DEFAULT SYSDATETIME(),

    CONSTRAINT pk_notifications PRIMARY KEY (notification_id),
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES dbo.users(user_id),
    CONSTRAINT ck_notifications_notification_type CHECK (notification_type IN (N'EMAIL', N'SMS', N'SYSTEM'))
);

CREATE TABLE dbo.reviews (
    review_id BIGINT IDENTITY(1,1) NOT NULL,
    customer_id BIGINT NOT NULL,
    food_id BIGINT NULL,
    order_id BIGINT NULL,
    rating INT NOT NULL,
    comment NVARCHAR(MAX) NULL,
    created_at DATETIME2(0) NOT NULL CONSTRAINT df_reviews_created_at DEFAULT SYSDATETIME(),

    CONSTRAINT pk_reviews PRIMARY KEY (review_id),
    CONSTRAINT fk_reviews_customer FOREIGN KEY (customer_id) REFERENCES dbo.users(user_id),
    CONSTRAINT fk_reviews_food FOREIGN KEY (food_id) REFERENCES dbo.foods(food_id),
    CONSTRAINT fk_reviews_order FOREIGN KEY (order_id) REFERENCES dbo.orders(order_id),
    CONSTRAINT ck_reviews_rating CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT ck_reviews_target CHECK (food_id IS NOT NULL OR order_id IS NOT NULL)
);

CREATE TABLE dbo.support_tickets (
    ticket_id BIGINT IDENTITY(1,1) NOT NULL,
    customer_id BIGINT NOT NULL,
    staff_id BIGINT NULL,
    subject NVARCHAR(200) NOT NULL,
    ticket_status NVARCHAR(30) NOT NULL CONSTRAINT df_support_tickets_ticket_status DEFAULT N'OPEN',
    created_at DATETIME2(0) NOT NULL CONSTRAINT df_support_tickets_created_at DEFAULT SYSDATETIME(),
    closed_at DATETIME2(0) NULL,

    CONSTRAINT pk_support_tickets PRIMARY KEY (ticket_id),
    CONSTRAINT fk_support_tickets_customer FOREIGN KEY (customer_id) REFERENCES dbo.users(user_id),
    CONSTRAINT fk_support_tickets_staff FOREIGN KEY (staff_id) REFERENCES dbo.users(user_id),
    CONSTRAINT ck_support_tickets_ticket_status CHECK (ticket_status IN (N'OPEN', N'IN_PROGRESS', N'CLOSED', N'CANCELLED')),
    CONSTRAINT ck_support_tickets_closed_at CHECK (closed_at IS NULL OR closed_at >= created_at)
);

CREATE TABLE dbo.support_messages (
    message_id BIGINT IDENTITY(1,1) NOT NULL,
    ticket_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    message_content NVARCHAR(MAX) NOT NULL,
    sent_at DATETIME2(0) NOT NULL CONSTRAINT df_support_messages_sent_at DEFAULT SYSDATETIME(),

    CONSTRAINT pk_support_messages PRIMARY KEY (message_id),
    CONSTRAINT fk_support_messages_ticket FOREIGN KEY (ticket_id) REFERENCES dbo.support_tickets(ticket_id) ON DELETE CASCADE,
    CONSTRAINT fk_support_messages_sender FOREIGN KEY (sender_id) REFERENCES dbo.users(user_id)
);

/* =========================================================
   6. AI Features
   ========================================================= */
CREATE TABLE dbo.ai_chat_sessions (
    session_id BIGINT IDENTITY(1,1) NOT NULL,
    customer_id BIGINT NOT NULL,
    started_at DATETIME2(0) NOT NULL CONSTRAINT df_ai_chat_sessions_started_at DEFAULT SYSDATETIME(),
    ended_at DATETIME2(0) NULL,
    session_status NVARCHAR(20) NOT NULL CONSTRAINT df_ai_chat_sessions_session_status DEFAULT N'ACTIVE',

    CONSTRAINT pk_ai_chat_sessions PRIMARY KEY (session_id),
    CONSTRAINT fk_ai_chat_sessions_customer FOREIGN KEY (customer_id) REFERENCES dbo.users(user_id),
    CONSTRAINT ck_ai_chat_sessions_session_status CHECK (session_status IN (N'ACTIVE', N'CLOSED')),
    CONSTRAINT ck_ai_chat_sessions_time CHECK (ended_at IS NULL OR ended_at >= started_at)
);

CREATE TABLE dbo.ai_chat_messages (
    message_id BIGINT IDENTITY(1,1) NOT NULL,
    session_id BIGINT NOT NULL,
    sender_type NVARCHAR(20) NOT NULL,
    message_content NVARCHAR(MAX) NOT NULL,
    sent_at DATETIME2(0) NOT NULL CONSTRAINT df_ai_chat_messages_sent_at DEFAULT SYSDATETIME(),

    CONSTRAINT pk_ai_chat_messages PRIMARY KEY (message_id),
    CONSTRAINT fk_ai_chat_messages_session FOREIGN KEY (session_id) REFERENCES dbo.ai_chat_sessions(session_id) ON DELETE CASCADE,
    CONSTRAINT ck_ai_chat_messages_sender_type CHECK (sender_type IN (N'CUSTOMER', N'AI', N'SYSTEM'))
);

CREATE TABLE dbo.food_recommendations (
    recommendation_id BIGINT IDENTITY(1,1) NOT NULL,
    customer_id BIGINT NOT NULL,
    food_id BIGINT NOT NULL,
    reason NVARCHAR(MAX) NULL,
    score DECIMAL(5,4) NULL,
    created_at DATETIME2(0) NOT NULL CONSTRAINT df_food_recommendations_created_at DEFAULT SYSDATETIME(),
    is_clicked BIT NOT NULL CONSTRAINT df_food_recommendations_is_clicked DEFAULT 0,

    CONSTRAINT pk_food_recommendations PRIMARY KEY (recommendation_id),
    CONSTRAINT fk_food_recommendations_customer FOREIGN KEY (customer_id) REFERENCES dbo.users(user_id),
    CONSTRAINT fk_food_recommendations_food FOREIGN KEY (food_id) REFERENCES dbo.foods(food_id),
    CONSTRAINT ck_food_recommendations_score CHECK (score IS NULL OR (score >= 0 AND score <= 1))
);

/* =========================================================
   7. Useful indexes for foreign keys and search fields
   ========================================================= */
CREATE INDEX ix_foods_category_id ON dbo.foods(category_id);
CREATE INDEX ix_food_images_food_id ON dbo.food_images(food_id);
CREATE INDEX ix_carts_customer_id ON dbo.carts(customer_id);
CREATE INDEX ix_cart_items_cart_id ON dbo.cart_items(cart_id);
CREATE INDEX ix_cart_items_food_id ON dbo.cart_items(food_id);
CREATE INDEX ix_orders_customer_id ON dbo.orders(customer_id);
CREATE INDEX ix_orders_staff_id ON dbo.orders(staff_id);
CREATE INDEX ix_orders_coupon_id ON dbo.orders(coupon_id);
CREATE INDEX ix_order_items_order_id ON dbo.order_items(order_id);
CREATE INDEX ix_order_items_food_id ON dbo.order_items(food_id);
CREATE INDEX ix_payments_order_id ON dbo.payments(order_id);
CREATE INDEX ix_kitchen_tasks_order_item_id ON dbo.kitchen_tasks(order_item_id);
CREATE INDEX ix_kitchen_tasks_kitchen_user_id ON dbo.kitchen_tasks(kitchen_user_id);
CREATE INDEX ix_reservations_customer_id ON dbo.reservations(customer_id);
CREATE INDEX ix_reservations_table_id ON dbo.reservations(table_id);
CREATE INDEX ix_reservations_approved_by ON dbo.reservations(approved_by);
CREATE INDEX ix_notifications_user_id ON dbo.notifications(user_id);
CREATE INDEX ix_reviews_customer_id ON dbo.reviews(customer_id);
CREATE INDEX ix_reviews_food_id ON dbo.reviews(food_id);
CREATE INDEX ix_reviews_order_id ON dbo.reviews(order_id);
CREATE INDEX ix_support_tickets_customer_id ON dbo.support_tickets(customer_id);
CREATE INDEX ix_support_tickets_staff_id ON dbo.support_tickets(staff_id);
CREATE INDEX ix_support_messages_ticket_id ON dbo.support_messages(ticket_id);
CREATE INDEX ix_support_messages_sender_id ON dbo.support_messages(sender_id);
CREATE INDEX ix_ai_chat_sessions_customer_id ON dbo.ai_chat_sessions(customer_id);
CREATE INDEX ix_ai_chat_messages_session_id ON dbo.ai_chat_messages(session_id);
CREATE INDEX ix_food_recommendations_customer_id ON dbo.food_recommendations(customer_id);
CREATE INDEX ix_food_recommendations_food_id ON dbo.food_recommendations(food_id);

/* SQL Server allows only one NULL with a normal UNIQUE constraint in many practical cases.
   This filtered unique index allows many NULL transaction_code values,
   but still prevents duplicate non-null transaction codes. */
CREATE UNIQUE INDEX ux_payments_transaction_code_not_null
ON dbo.payments(transaction_code)
WHERE transaction_code IS NOT NULL;

PRINT N'Schema created successfully.';
