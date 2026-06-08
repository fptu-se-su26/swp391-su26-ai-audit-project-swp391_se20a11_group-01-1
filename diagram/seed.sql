/*
    seed.sql
    Member 2 - Sample Data for Restaurant Management System
    Database: Microsoft SQL Server / SQL Server Management Studio 21

    Requirement:
    - Seed sample data for users, roles, foods, tables, coupons.
    - This file should be executed after schema.sql.

    Notes:
    - Passwords below are demo hashes only, not real BCrypt hashes.
    - The script is written to avoid duplicate insert for unique/common seed records.
*/

SET NOCOUNT ON;

BEGIN TRY
    BEGIN TRANSACTION;

    /* =========================================================
       1. Roles
       ========================================================= */
    IF NOT EXISTS (SELECT 1 FROM dbo.roles WHERE role_name = N'CUSTOMER')
        INSERT INTO dbo.roles (role_name, description)
        VALUES (N'CUSTOMER', N'Customer who can view menu, order food, reserve tables and review services.');

    IF NOT EXISTS (SELECT 1 FROM dbo.roles WHERE role_name = N'STAFF')
        INSERT INTO dbo.roles (role_name, description)
        VALUES (N'STAFF', N'Restaurant staff who can process orders, tables, payments and customer support.');

    IF NOT EXISTS (SELECT 1 FROM dbo.roles WHERE role_name = N'KITCHEN')
        INSERT INTO dbo.roles (role_name, description)
        VALUES (N'KITCHEN', N'Kitchen employee who can receive and update cooking orders.');

    IF NOT EXISTS (SELECT 1 FROM dbo.roles WHERE role_name = N'ADMIN')
        INSERT INTO dbo.roles (role_name, description)
        VALUES (N'ADMIN', N'Administrator who can configure accounts, menu, coupons and reports.');

    /* =========================================================
       2. Permissions - optional but useful for role-based access
       ========================================================= */
    IF NOT EXISTS (SELECT 1 FROM dbo.permissions WHERE permission_name = N'VIEW_MENU')
        INSERT INTO dbo.permissions (permission_name, description)
        VALUES (N'VIEW_MENU', N'View food menu and food detail.');

    IF NOT EXISTS (SELECT 1 FROM dbo.permissions WHERE permission_name = N'PLACE_ORDER')
        INSERT INTO dbo.permissions (permission_name, description)
        VALUES (N'PLACE_ORDER', N'Create food orders from cart.');

    IF NOT EXISTS (SELECT 1 FROM dbo.permissions WHERE permission_name = N'RESERVE_TABLE')
        INSERT INTO dbo.permissions (permission_name, description)
        VALUES (N'RESERVE_TABLE', N'Reserve a restaurant table.');

    IF NOT EXISTS (SELECT 1 FROM dbo.permissions WHERE permission_name = N'UPDATE_ORDER_STATUS')
        INSERT INTO dbo.permissions (permission_name, description)
        VALUES (N'UPDATE_ORDER_STATUS', N'Update order status.');

    IF NOT EXISTS (SELECT 1 FROM dbo.permissions WHERE permission_name = N'PROCESS_COOKING_ORDER')
        INSERT INTO dbo.permissions (permission_name, description)
        VALUES (N'PROCESS_COOKING_ORDER', N'Process kitchen tasks and update cooking status.');

    IF NOT EXISTS (SELECT 1 FROM dbo.permissions WHERE permission_name = N'UPDATE_FOOD_MENU')
        INSERT INTO dbo.permissions (permission_name, description)
        VALUES (N'UPDATE_FOOD_MENU', N'Create, update or disable food menu items.');

    IF NOT EXISTS (SELECT 1 FROM dbo.permissions WHERE permission_name = N'CONFIGURE_COUPONS')
        INSERT INTO dbo.permissions (permission_name, description)
        VALUES (N'CONFIGURE_COUPONS', N'Create and update coupon information.');

    IF NOT EXISTS (SELECT 1 FROM dbo.permissions WHERE permission_name = N'VIEW_REVENUE')
        INSERT INTO dbo.permissions (permission_name, description)
        VALUES (N'VIEW_REVENUE', N'View revenue reports and dashboard statistics.');

    DECLARE @roleCustomer BIGINT = (SELECT role_id FROM dbo.roles WHERE role_name = N'CUSTOMER');
    DECLARE @roleStaff    BIGINT = (SELECT role_id FROM dbo.roles WHERE role_name = N'STAFF');
    DECLARE @roleKitchen  BIGINT = (SELECT role_id FROM dbo.roles WHERE role_name = N'KITCHEN');
    DECLARE @roleAdmin    BIGINT = (SELECT role_id FROM dbo.roles WHERE role_name = N'ADMIN');

    /* Role - Permission mapping */
    INSERT INTO dbo.role_permissions (role_id, permission_id)
    SELECT @roleCustomer, p.permission_id
    FROM dbo.permissions p
    WHERE p.permission_name IN (N'VIEW_MENU', N'PLACE_ORDER', N'RESERVE_TABLE')
      AND NOT EXISTS (
          SELECT 1 FROM dbo.role_permissions rp
          WHERE rp.role_id = @roleCustomer AND rp.permission_id = p.permission_id
      );

    INSERT INTO dbo.role_permissions (role_id, permission_id)
    SELECT @roleStaff, p.permission_id
    FROM dbo.permissions p
    WHERE p.permission_name IN (N'VIEW_MENU', N'UPDATE_ORDER_STATUS')
      AND NOT EXISTS (
          SELECT 1 FROM dbo.role_permissions rp
          WHERE rp.role_id = @roleStaff AND rp.permission_id = p.permission_id
      );

    INSERT INTO dbo.role_permissions (role_id, permission_id)
    SELECT @roleKitchen, p.permission_id
    FROM dbo.permissions p
    WHERE p.permission_name IN (N'PROCESS_COOKING_ORDER')
      AND NOT EXISTS (
          SELECT 1 FROM dbo.role_permissions rp
          WHERE rp.role_id = @roleKitchen AND rp.permission_id = p.permission_id
      );

    INSERT INTO dbo.role_permissions (role_id, permission_id)
    SELECT @roleAdmin, p.permission_id
    FROM dbo.permissions p
    WHERE p.permission_name IN (N'VIEW_MENU', N'UPDATE_FOOD_MENU', N'CONFIGURE_COUPONS', N'VIEW_REVENUE', N'UPDATE_ORDER_STATUS')
      AND NOT EXISTS (
          SELECT 1 FROM dbo.role_permissions rp
          WHERE rp.role_id = @roleAdmin AND rp.permission_id = p.permission_id
      );

    /* =========================================================
       3. Users
       ========================================================= */
    IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE email = N'admin@restaurant.com')
        INSERT INTO dbo.users (full_name, email, password_hash, phone, address, status)
        VALUES (N'Nguyen Minh Admin', N'admin@restaurant.com', N'DEMO_HASH_admin123', N'0901000001', N'Office - Main Restaurant', N'ACTIVE');

    IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE email = N'staff@restaurant.com')
        INSERT INTO dbo.users (full_name, email, password_hash, phone, address, status)
        VALUES (N'Tran Thu Staff', N'staff@restaurant.com', N'DEMO_HASH_staff123', N'0901000002', N'Front Desk', N'ACTIVE');

    IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE email = N'kitchen@restaurant.com')
        INSERT INTO dbo.users (full_name, email, password_hash, phone, address, status)
        VALUES (N'Le Van Kitchen', N'kitchen@restaurant.com', N'DEMO_HASH_kitchen123', N'0901000003', N'Kitchen Area', N'ACTIVE');

    IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE email = N'customer1@gmail.com')
        INSERT INTO dbo.users (full_name, email, password_hash, phone, address, status)
        VALUES (N'Pham Anh Customer', N'customer1@gmail.com', N'DEMO_HASH_customer123', N'0901000004', N'Da Nang City', N'ACTIVE');

    IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE email = N'customer2@gmail.com')
        INSERT INTO dbo.users (full_name, email, password_hash, phone, address, status)
        VALUES (N'Hoang Bao Customer', N'customer2@gmail.com', N'DEMO_HASH_customer123', N'0901000005', N'Hai Chau, Da Nang', N'ACTIVE');

    DECLARE @adminUser    BIGINT = (SELECT user_id FROM dbo.users WHERE email = N'admin@restaurant.com');
    DECLARE @staffUser    BIGINT = (SELECT user_id FROM dbo.users WHERE email = N'staff@restaurant.com');
    DECLARE @kitchenUser  BIGINT = (SELECT user_id FROM dbo.users WHERE email = N'kitchen@restaurant.com');
    DECLARE @customerOne  BIGINT = (SELECT user_id FROM dbo.users WHERE email = N'customer1@gmail.com');
    DECLARE @customerTwo  BIGINT = (SELECT user_id FROM dbo.users WHERE email = N'customer2@gmail.com');

    IF NOT EXISTS (SELECT 1 FROM dbo.user_roles WHERE user_id = @adminUser AND role_id = @roleAdmin)
        INSERT INTO dbo.user_roles (user_id, role_id) VALUES (@adminUser, @roleAdmin);

    IF NOT EXISTS (SELECT 1 FROM dbo.user_roles WHERE user_id = @staffUser AND role_id = @roleStaff)
        INSERT INTO dbo.user_roles (user_id, role_id) VALUES (@staffUser, @roleStaff);

    IF NOT EXISTS (SELECT 1 FROM dbo.user_roles WHERE user_id = @kitchenUser AND role_id = @roleKitchen)
        INSERT INTO dbo.user_roles (user_id, role_id) VALUES (@kitchenUser, @roleKitchen);

    IF NOT EXISTS (SELECT 1 FROM dbo.user_roles WHERE user_id = @customerOne AND role_id = @roleCustomer)
        INSERT INTO dbo.user_roles (user_id, role_id) VALUES (@customerOne, @roleCustomer);

    IF NOT EXISTS (SELECT 1 FROM dbo.user_roles WHERE user_id = @customerTwo AND role_id = @roleCustomer)
        INSERT INTO dbo.user_roles (user_id, role_id) VALUES (@customerTwo, @roleCustomer);

    /* =========================================================
       4. Food Categories
       ========================================================= */
    IF NOT EXISTS (SELECT 1 FROM dbo.categories WHERE category_name = N'Appetizer')
        INSERT INTO dbo.categories (category_name, description, status)
        VALUES (N'Appetizer', N'Starter dishes served before the main course.', N'ACTIVE');

    IF NOT EXISTS (SELECT 1 FROM dbo.categories WHERE category_name = N'Main Dish')
        INSERT INTO dbo.categories (category_name, description, status)
        VALUES (N'Main Dish', N'Popular main dishes of the restaurant.', N'ACTIVE');

    IF NOT EXISTS (SELECT 1 FROM dbo.categories WHERE category_name = N'Hotpot')
        INSERT INTO dbo.categories (category_name, description, status)
        VALUES (N'Hotpot', N'Hotpot sets for groups and family meals.', N'ACTIVE');

    IF NOT EXISTS (SELECT 1 FROM dbo.categories WHERE category_name = N'Noodle and Rice')
        INSERT INTO dbo.categories (category_name, description, status)
        VALUES (N'Noodle and Rice', N'Rice and noodle dishes.', N'ACTIVE');

    IF NOT EXISTS (SELECT 1 FROM dbo.categories WHERE category_name = N'Drink')
        INSERT INTO dbo.categories (category_name, description, status)
        VALUES (N'Drink', N'Beverages and soft drinks.', N'ACTIVE');

    IF NOT EXISTS (SELECT 1 FROM dbo.categories WHERE category_name = N'Dessert')
        INSERT INTO dbo.categories (category_name, description, status)
        VALUES (N'Dessert', N'Sweet dishes served after the meal.', N'ACTIVE');

    DECLARE @catAppetizer BIGINT = (SELECT category_id FROM dbo.categories WHERE category_name = N'Appetizer');
    DECLARE @catMain      BIGINT = (SELECT category_id FROM dbo.categories WHERE category_name = N'Main Dish');
    DECLARE @catHotpot    BIGINT = (SELECT category_id FROM dbo.categories WHERE category_name = N'Hotpot');
    DECLARE @catNoodle    BIGINT = (SELECT category_id FROM dbo.categories WHERE category_name = N'Noodle and Rice');
    DECLARE @catDrink     BIGINT = (SELECT category_id FROM dbo.categories WHERE category_name = N'Drink');
    DECLARE @catDessert   BIGINT = (SELECT category_id FROM dbo.categories WHERE category_name = N'Dessert');

    /* =========================================================
       5. Foods
       ========================================================= */
    IF NOT EXISTS (SELECT 1 FROM dbo.foods WHERE food_name = N'Spring Rolls')
        INSERT INTO dbo.foods (category_id, food_name, description, price, availability_status, estimated_cooking_time)
        VALUES (@catAppetizer, N'Spring Rolls', N'Crispy fried spring rolls served with fish sauce.', 45000, N'AVAILABLE', 10);

    IF NOT EXISTS (SELECT 1 FROM dbo.foods WHERE food_name = N'Green Papaya Salad')
        INSERT INTO dbo.foods (category_id, food_name, description, price, availability_status, estimated_cooking_time)
        VALUES (@catAppetizer, N'Green Papaya Salad', N'Fresh green papaya salad with herbs and roasted peanuts.', 55000, N'AVAILABLE', 12);

    IF NOT EXISTS (SELECT 1 FROM dbo.foods WHERE food_name = N'Grilled Chicken Rice')
        INSERT INTO dbo.foods (category_id, food_name, description, price, availability_status, estimated_cooking_time)
        VALUES (@catMain, N'Grilled Chicken Rice', N'Grilled chicken served with rice, vegetables and special sauce.', 79000, N'AVAILABLE', 18);

    IF NOT EXISTS (SELECT 1 FROM dbo.foods WHERE food_name = N'Beef Steak with Pepper Sauce')
        INSERT INTO dbo.foods (category_id, food_name, description, price, availability_status, estimated_cooking_time)
        VALUES (@catMain, N'Beef Steak with Pepper Sauce', N'Beef steak cooked with black pepper sauce and vegetables.', 159000, N'AVAILABLE', 25);

    IF NOT EXISTS (SELECT 1 FROM dbo.foods WHERE food_name = N'Seafood Fried Rice')
        INSERT INTO dbo.foods (category_id, food_name, description, price, availability_status, estimated_cooking_time)
        VALUES (@catNoodle, N'Seafood Fried Rice', N'Fried rice with shrimp, squid, egg and vegetables.', 89000, N'AVAILABLE', 15);

    IF NOT EXISTS (SELECT 1 FROM dbo.foods WHERE food_name = N'Beef Pho')
        INSERT INTO dbo.foods (category_id, food_name, description, price, availability_status, estimated_cooking_time)
        VALUES (@catNoodle, N'Beef Pho', N'Vietnamese noodle soup with beef and herbs.', 65000, N'AVAILABLE', 12);

    IF NOT EXISTS (SELECT 1 FROM dbo.foods WHERE food_name = N'Thai Seafood Hotpot')
        INSERT INTO dbo.foods (category_id, food_name, description, price, availability_status, estimated_cooking_time)
        VALUES (@catHotpot, N'Thai Seafood Hotpot', N'Spicy Thai hotpot with shrimp, squid, fish and vegetables.', 299000, N'AVAILABLE', 30);

    IF NOT EXISTS (SELECT 1 FROM dbo.foods WHERE food_name = N'Mushroom Beef Hotpot')
        INSERT INTO dbo.foods (category_id, food_name, description, price, availability_status, estimated_cooking_time)
        VALUES (@catHotpot, N'Mushroom Beef Hotpot', N'Hotpot with sliced beef, mushroom and fresh vegetables.', 279000, N'AVAILABLE', 30);

    IF NOT EXISTS (SELECT 1 FROM dbo.foods WHERE food_name = N'Lemon Tea')
        INSERT INTO dbo.foods (category_id, food_name, description, price, availability_status, estimated_cooking_time)
        VALUES (@catDrink, N'Lemon Tea', N'Fresh lemon tea served with ice.', 25000, N'AVAILABLE', 5);

    IF NOT EXISTS (SELECT 1 FROM dbo.foods WHERE food_name = N'Orange Juice')
        INSERT INTO dbo.foods (category_id, food_name, description, price, availability_status, estimated_cooking_time)
        VALUES (@catDrink, N'Orange Juice', N'Fresh orange juice.', 35000, N'AVAILABLE', 5);

    IF NOT EXISTS (SELECT 1 FROM dbo.foods WHERE food_name = N'Mineral Water')
        INSERT INTO dbo.foods (category_id, food_name, description, price, availability_status, estimated_cooking_time)
        VALUES (@catDrink, N'Mineral Water', N'Bottle of mineral water.', 15000, N'AVAILABLE', 1);

    IF NOT EXISTS (SELECT 1 FROM dbo.foods WHERE food_name = N'Caramel Flan')
        INSERT INTO dbo.foods (category_id, food_name, description, price, availability_status, estimated_cooking_time)
        VALUES (@catDessert, N'Caramel Flan', N'Soft caramel custard dessert.', 30000, N'AVAILABLE', 5);

    IF NOT EXISTS (SELECT 1 FROM dbo.foods WHERE food_name = N'Fruit Yogurt')
        INSERT INTO dbo.foods (category_id, food_name, description, price, availability_status, estimated_cooking_time)
        VALUES (@catDessert, N'Fruit Yogurt', N'Yogurt mixed with seasonal fruits.', 39000, N'AVAILABLE', 5);

    /* Optional sample images for foods */
    INSERT INTO dbo.food_images (food_id, image_url, is_primary)
    SELECT f.food_id, CONCAT(N'https://example.com/images/', LOWER(REPLACE(f.food_name, N' ', N'-')), N'.jpg'), 1
    FROM dbo.foods f
    WHERE f.food_name IN (
        N'Spring Rolls', N'Grilled Chicken Rice', N'Beef Pho', N'Thai Seafood Hotpot',
        N'Lemon Tea', N'Caramel Flan'
    )
    AND NOT EXISTS (
        SELECT 1 FROM dbo.food_images fi
        WHERE fi.food_id = f.food_id AND fi.is_primary = 1
    );

    /* =========================================================
       6. Restaurant Tables
       ========================================================= */
    IF NOT EXISTS (SELECT 1 FROM dbo.restaurant_tables WHERE table_number = N'A01')
        INSERT INTO dbo.restaurant_tables (table_number, capacity, table_status, location)
        VALUES (N'A01', 2, N'AVAILABLE', N'Ground Floor - Window Area');

    IF NOT EXISTS (SELECT 1 FROM dbo.restaurant_tables WHERE table_number = N'A02')
        INSERT INTO dbo.restaurant_tables (table_number, capacity, table_status, location)
        VALUES (N'A02', 2, N'AVAILABLE', N'Ground Floor - Window Area');

    IF NOT EXISTS (SELECT 1 FROM dbo.restaurant_tables WHERE table_number = N'B01')
        INSERT INTO dbo.restaurant_tables (table_number, capacity, table_status, location)
        VALUES (N'B01', 4, N'AVAILABLE', N'Ground Floor - Main Area');

    IF NOT EXISTS (SELECT 1 FROM dbo.restaurant_tables WHERE table_number = N'B02')
        INSERT INTO dbo.restaurant_tables (table_number, capacity, table_status, location)
        VALUES (N'B02', 4, N'OCCUPIED', N'Ground Floor - Main Area');

    IF NOT EXISTS (SELECT 1 FROM dbo.restaurant_tables WHERE table_number = N'B03')
        INSERT INTO dbo.restaurant_tables (table_number, capacity, table_status, location)
        VALUES (N'B03', 4, N'RESERVED', N'Ground Floor - Main Area');

    IF NOT EXISTS (SELECT 1 FROM dbo.restaurant_tables WHERE table_number = N'C01')
        INSERT INTO dbo.restaurant_tables (table_number, capacity, table_status, location)
        VALUES (N'C01', 6, N'AVAILABLE', N'Second Floor - Family Area');

    IF NOT EXISTS (SELECT 1 FROM dbo.restaurant_tables WHERE table_number = N'C02')
        INSERT INTO dbo.restaurant_tables (table_number, capacity, table_status, location)
        VALUES (N'C02', 6, N'AVAILABLE', N'Second Floor - Family Area');

    IF NOT EXISTS (SELECT 1 FROM dbo.restaurant_tables WHERE table_number = N'VIP01')
        INSERT INTO dbo.restaurant_tables (table_number, capacity, table_status, location)
        VALUES (N'VIP01', 10, N'AVAILABLE', N'Private Room');

    IF NOT EXISTS (SELECT 1 FROM dbo.restaurant_tables WHERE table_number = N'VIP02')
        INSERT INTO dbo.restaurant_tables (table_number, capacity, table_status, location)
        VALUES (N'VIP02', 12, N'OUT_OF_SERVICE', N'Private Room');

    /* =========================================================
       7. Coupons
       ========================================================= */
    IF NOT EXISTS (SELECT 1 FROM dbo.coupons WHERE code = N'WELCOME10')
        INSERT INTO dbo.coupons (code, discount_type, discount_value, min_order_amount, start_date, end_date, status)
        VALUES (N'WELCOME10', N'PERCENTAGE', 10, 100000, '2026-01-01 00:00:00', '2026-12-31 23:59:59', N'ACTIVE');

    IF NOT EXISTS (SELECT 1 FROM dbo.coupons WHERE code = N'STUDENT20K')
        INSERT INTO dbo.coupons (code, discount_type, discount_value, min_order_amount, start_date, end_date, status)
        VALUES (N'STUDENT20K', N'FIXED_AMOUNT', 20000, 120000, '2026-01-01 00:00:00', '2026-12-31 23:59:59', N'ACTIVE');

    IF NOT EXISTS (SELECT 1 FROM dbo.coupons WHERE code = N'FAMILY15')
        INSERT INTO dbo.coupons (code, discount_type, discount_value, min_order_amount, start_date, end_date, status)
        VALUES (N'FAMILY15', N'PERCENTAGE', 15, 300000, '2026-01-01 00:00:00', '2026-12-31 23:59:59', N'ACTIVE');

    IF NOT EXISTS (SELECT 1 FROM dbo.coupons WHERE code = N'HOT50K')
        INSERT INTO dbo.coupons (code, discount_type, discount_value, min_order_amount, start_date, end_date, status)
        VALUES (N'HOT50K', N'FIXED_AMOUNT', 50000, 350000, '2026-06-01 00:00:00', '2026-08-31 23:59:59', N'ACTIVE');

    IF NOT EXISTS (SELECT 1 FROM dbo.coupons WHERE code = N'OLDPROMO')
        INSERT INTO dbo.coupons (code, discount_type, discount_value, min_order_amount, start_date, end_date, status)
        VALUES (N'OLDPROMO', N'PERCENTAGE', 5, 100000, '2025-01-01 00:00:00', '2025-12-31 23:59:59', N'EXPIRED');

    COMMIT TRANSACTION;
    PRINT N'Seed data inserted successfully.';
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0
        ROLLBACK TRANSACTION;

    DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
    DECLARE @ErrorSeverity INT = ERROR_SEVERITY();
    DECLARE @ErrorState INT = ERROR_STATE();

    RAISERROR(@ErrorMessage, @ErrorSeverity, @ErrorState);
END CATCH;
