# Physical Database Design

## 1. Database Engine
* **RDBMS**: MySQL 8.x
* **Storage Engine**: InnoDB
* **Character Set**: `utf8mb4`
* **Collation**: `utf8mb4_unicode_ci`
* **Timezone**: UTC

## 2. Table Design Summary

| Table Name | Purpose | PK | FK | UK | Indexes | Soft Delete | Audit | Version |
|---|---|---|---|---|---|---|---|---|
| `users` | Tài khoản người dùng | `id` | - | `uk_user_email`, `uk_user_username` | `idx_user_email` | Yes | Yes | Yes |
| `roles` | Vai trò hệ thống | `id` | - | `uk_role_name` | - | No | Yes | No |
| `user_roles` | Phân quyền User-Role | `(user_id, role_id)` | `user_id`, `role_id` | - | - | Hard | No | No |
| `refresh_tokens` | Quản lý token | `id` | `user_id` | `uk_refresh_token_hash` | `idx_refresh_token_user`, `idx_refresh_token_expires_at` | No | Yes | Yes |
| `customer_profiles` | Hồ sơ khách hàng | `id` | `user_id` | `uk_customer_user_id` | - | Yes | Yes | No |
| `employees` | Hồ sơ nhân viên | `id` | `user_id` | `uk_employee_user_id` | - | Yes | Yes | No |
| `food_categories` | Danh mục món ăn | `id` | - | - | - | Yes | Yes | No |
| `food_items` | Thông tin món ăn | `id` | `category_id` | - | `idx_food_item_category` | Yes | Yes | Yes |
| `carts` | Giỏ hàng tạm | `id` | `user_id` | `uk_cart_user_id` | - | Hard | Yes | No |
| `cart_items` | Món trong giỏ | `id` | `cart_id`, `food_item_id` | - | - | Hard | Yes | No |
| `coupons` | Mã giảm giá | `id` | - | `uk_coupon_code` | `idx_coupon_code` | Yes | Yes | Yes |
| `coupon_usages` | Lịch sử dùng mã | `id` | `coupon_id`, `user_id`, `order_id` | `uk_coupon_usage_order` | - | No | Yes | No |
| `restaurant_tables` | Sơ đồ bàn | `id` | - | `uk_table_number` | - | Yes | Yes | No |
| `reservations` | Đặt bàn | `id` | `customer_id`, `table_id` | - | `idx_reservation_time` | Yes | Yes | Yes |
| `restaurant_orders` | Đơn hàng tổng | `id` | `customer_id`, `table_id`, `coupon_id`| - | `idx_order_status` | No | Yes | Yes |
| `order_items` | Món trong đơn | `id` | `order_id`, `food_item_id` | - | - | No | Yes | No |
| `payments` | Giao dịch thanh toán | `id` | `order_id` | - | `idx_payment_status` | No | Yes | Yes |
| `invoices` | Hóa đơn tài chính | `id` | `payment_id` | `uk_invoice_number`, `uk_invoice_payment_id` | - | No | Yes | No |

## 3. Money and Decimal Rules
* Dùng `DECIMAL(19,2)` cho tất cả các cột liên quan đến tiền tệ (`price`, `sub_total`, `discount_amount`, `total_amount`, `amount`, `tax_amount`).
* Tuyệt đối không dùng `FLOAT` hay `DOUBLE` để tránh sai số dấu phẩy động.

## 4. DateTime Rules
* Dùng `DATETIME(6)` để lưu trữ chính xác đến microsecond, phù hợp với Java `LocalDateTime`/`Instant`.
* Lưu dưới dạng múi giờ UTC.

## 5. Constraint Strategy
* **PK (Primary Key)**: `id BIGINT AUTO_INCREMENT PRIMARY KEY`.
* **FK (Foreign Key)**: Ràng buộc tính toàn vẹn dữ liệu. Các bảng cấu hình (Master Data) khi xoá sẽ `RESTRICT`, các bảng phụ như giỏ hàng sẽ `CASCADE`.
* **UK (Unique Key)**: Bắt buộc ở database level để chống trùng lặp dữ liệu quan trọng như Email, Username, Coupon Code, Invoice Number.
* **CHECK**: Dùng CHECK constraint (trên MySQL 8+) cho các điều kiện đơn giản (VD: `price >= 0`, `quantity > 0`).

## 6. Index Strategy
* `idx_user_email`: Tăng tốc tìm kiếm đăng nhập.
* `idx_coupon_code`: Check coupon nhanh.
* `idx_reservation_time`: Truy vấn overlap đặt bàn.
* `idx_order_status`, `idx_payment_status`: Dùng cho dashboard và báo cáo.

## 7. Physical ERD Notes
* **Xung đột từ khoá**: Thay vì dùng tên bảng `orders` (từ khoá chuẩn của SQL), bảng đơn hàng được đổi thành `restaurant_orders` để an toàn và nhất quán trên mọi DB Engine.
* Mọi boolean field được chuyển thành `TINYINT(1)` (chuẩn MySQL).

## 8. Migration Plan
Quản lý database versioning qua Flyway.
Đề xuất chia nhỏ các script migration theo domain để dễ quản lý:

V1__create_auth_tables.sql
- users
- roles
- user_roles
- refresh_tokens
- customer_profiles
- employees

V2__create_menu_tables.sql
- food_categories
- food_items

V3__create_cart_tables.sql
- carts
- cart_items
- coupons

V4__create_reservation_tables.sql
- restaurant_tables
- reservations

V5__create_order_payment_invoice_tables.sql
- restaurant_orders
- order_items
- coupon_usages
- payments
- invoices

V6__add_unit_price_to_cart_items.sql
- (ALTER) cart_items

V7__add_name_description_to_coupons.sql
- (ALTER) coupons


*Ghi chú:*
`restaurant_orders` references `restaurant_tables`, therefore reservation/table migration must run before order/payment/invoice migration.
`coupon_usages` references `restaurant_orders`, therefore it belongs to V5 instead of V3.

## 9. Seed Data Plan
Sau khi schema hoàn tất, cần có script seed dữ liệu cơ bản để chạy ứng dụng (Sẽ tạo ở script riêng).
* `R__01_seed_roles.sql`: Tạo ROLE_ADMIN, ROLE_STAFF, ROLE_CUSTOMER, ROLE_KITCHEN.
* `R__02_seed_admin_user.sql`: Khởi tạo tài khoản root admin.
* `R__03_seed_food_categories.sql`: Các danh mục món cơ bản.
* `R__04_seed_tables.sql`: Sơ đồ bàn cơ sở.

## 10. Validation Checklist
- [x] Bảng `restaurant_orders` thay vì `orders`.
- [x] Tất cả các bảng master có Soft Delete (`deleted_at`, `deleted_by`).
- [x] Bảng giao dịch có Version (`version BIGINT DEFAULT 0`) cho Optimistic Locking.
- [x] Mọi bảng có đủ 4 Audit fields.
- [x] Tiền dùng `DECIMAL(19,2)`.
- [x] FK có chỉ mục (index) đúng và quy tắc ON DELETE phù hợp.

## 11. Final Reviews

### REVIEW 1 — Reserved Keywords
- Không có bảng nào dùng reserved keywords nguy hiểm.
- `user` -> Đổi thành `users`.
- `order` -> Đổi thành `restaurant_orders`.
- `table` -> Đổi thành `restaurant_tables`.
- `role` -> Đổi thành `roles`.
- Cột trạng thái dùng `status`, `order_status`, `payment_status` (hợp lệ).
- Cột `version` (hợp lệ).

### REVIEW 2 — RefreshToken Consistency
- **Relationship**: `user_id` FK liên kết tới bảng `users` (Đúng).
- **Unique**: `uk_refresh_token_hash` trên cột `token_hash` (Đúng).
- **Index**: Có `idx_refresh_token_user` và `idx_refresh_token_expires_at` (Đúng).
- **Audit fields**: Đầy đủ 4 cột (Đúng).
- **Version field**: Đã có `version BIGINT DEFAULT 0` (Đúng).

### REVIEW 3 — Payment & Invoice Consistency
1. **Một Order có bao nhiêu Payment**: Có thể có nhiều Payment (1:N) do hỗ trợ retry thanh toán nếu giao dịch trước đó thất bại.
2. **Một Order có bao nhiêu Invoice**: Tối đa 1 Invoice. Rule kinh doanh quy định 1 order thành công chỉ xuất 1 hoá đơn.
3. **Có cho retry payment không**: CÓ. Bằng cách tạo bản ghi `Payment` mới tham chiếu cùng `order_id`.
4. **Duplicate payment xử lý thế nào**: DB không tự cản được do quan hệ 1:N. Phải xử lý bằng Optimistic Locking (`version`) trên bảng `restaurant_orders` tại Service layer.
5. **Duplicate invoice xử lý thế nào**: Bảng `invoices` có `uk_invoice_payment_id`. Đảm bảo 1 Payment thành công = 1 Invoice.
6. **Unique constraints bảo vệ**: `uk_invoice_payment_id` và `uk_invoice_number`.

### REVIEW 4 — Coupon Consistency
1. **User có thể dùng coupon nhiều lần không**: Phụ thuộc quy định business. DB thiết kế bảng `coupon_usages` cho phép Service query đếm số lần dùng.
2. **Coupon global khác coupon per-user thế nào**: Schema hiện thiết kế Global Coupon (có `usage_limit` tổng). Để giới hạn per-user, backend đếm lịch sử trong `coupon_usages`.
3. **Constraint bảo vệ usage limit**: Optimistic locking (`version` trên bảng `coupons`). Không thể dùng constraint DB cứng.
4. **Constraint xử lý ở service**: Việc check ngày hết hạn (`expires_at`), kiểm tra số lượt dùng/user, và điều kiện `min_order_value`.

### REVIEW 5 — Reservation Consistency
1. **DB có tự chống booking trùng giờ không**: KHÔNG tự chống được bằng Constraint thông thường của MySQL.
2. **Cần service validation gì**: Cần query tìm các reservation có `reservation_time` +- 2 giờ với cùng `table_id` và status = `CONFIRMED` hoặc `SEATED`.
3. **Có cần unique constraint đặc biệt không**: KHÔNG. (Chỉ PostgreSQL hỗ trợ Exclusion Constraint).
4. **Có cần lock khi tạo reservation không**: CÓ. Cần Pessimistic Lock (`SELECT FOR UPDATE`) trên bảng `restaurant_tables` khi check available.

### REVIEW 6 — Flyway Readiness
Schema có thể tách thành 4-5 file, tuy nhiên có một thay đổi nhỏ về luồng tham chiếu FK:
- `coupon_usages` có FK tham chiếu tới `restaurant_orders`, nên không thể tạo ở `V3__cart_coupon.sql`. Đã dời `coupon_usages` sang file `V4__order_payment_invoice_tables.sql` để đảm bảo không bị lỗi khóa ngoại khi chạy Flyway. (Đã sửa lại ở mục Migration Plan phía trên).

## 12. Final Schema Readiness Checklist
- [x] PK
- [x] FK
- [x] UK
- [x] Index
- [x] Audit Fields
- [x] Version Columns
- [x] Soft Delete
- [x] RefreshToken
- [x] Payment Rules
- [x] Invoice Rules
- [x] Coupon Rules
- [x] Reservation Rules
- [x] Flyway Ready
