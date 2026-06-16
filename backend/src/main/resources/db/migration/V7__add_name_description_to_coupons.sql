-- V7: Add missing name and description to coupons
ALTER TABLE coupons ADD COLUMN name VARCHAR(255);
ALTER TABLE coupons ADD COLUMN description TEXT;
