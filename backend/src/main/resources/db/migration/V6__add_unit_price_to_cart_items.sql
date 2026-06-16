-- V6: Add unit_price to cart_items for price snapshotting
ALTER TABLE cart_items ADD COLUMN unit_price DECIMAL(19,2) NOT NULL DEFAULT 0;
