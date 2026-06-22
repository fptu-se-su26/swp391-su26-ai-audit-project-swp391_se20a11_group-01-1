# Migration Consistency Review

This document assesses the consistency between Flyway migration scripts (V1 -> V7) and JPA entity mappings.

## 1. Migration Files Assessed
* `V1__init_schema.sql` - Core Users, Roles, User_Roles.
* `V2__create_menu_tables.sql` - Categories and Food_Items.
* `V3__create_cart_tables.sql` - Cart, Cart_Items, and Coupons.
* `V4__create_reservation_tables.sql` - Restaurant_Tables and Reservations.
* `V5__create_order_payment_invoice_tables.sql` - Restaurant_Orders, Order_Items, Payments, and Invoices.
* `V6__add_deleted_at_to_tables.sql` - Soft delete support for Categories and Foods.
* `V7__add_deleted_at_to_restaurant_tables.sql` - Soft delete support for Restaurant_Tables.

## 2. Entity Mapping Consistency
The entity mappings under `com.restaurant.management.entity` have been thoroughly verified against the physical database structure.

**Findings:**
- **Tables and Columns:** `@Table(name="...")` and `@Column(name="...")` annotations are precisely aligned with the snake_case naming conventions established in SQL scripts.
- **Enums:** Java enums (`OrderStatus`, `PaymentStatus`, `ReservationStatus`, `TableStatus`, `OrderType`, etc.) are securely mapped using `@Enumerated(EnumType.STRING)` to match `VARCHAR` fields in migrations.
- **Audit Columns:** Base entity classes properly map `created_at` and `updated_at` matching the `TIMESTAMP DEFAULT CURRENT_TIMESTAMP` definitions.
- **Data Types:** Financial data types (`DECIMAL(10,2)`) are reliably mapped to `java.math.BigDecimal`. Primary keys (`BIGINT`) map to `Long`.
- **Relationships:** `@ManyToOne`, `@OneToMany`, and `@OneToOne` mappings contain correct `@JoinColumn` definitions reflecting foreign keys in Flyway.
- **Soft Deletes:** Entities such as `Category`, `FoodItem`, and `RestaurantTable` map the `deleted_at` column correctly to handle V6 and V7 migrations.

## 3. Recommended Actions
- **No missing mappings:** Every table and column defined in the SQL scripts is represented in the entity model.
- **No missing migrations:** Entities do not contain fields unsupported by the database schema.
- **No changes required:** The current migration sequence V1-V7 is perfectly synced with the entity architecture. No new migrations are necessary, nor should any old migrations be modified or deleted.
