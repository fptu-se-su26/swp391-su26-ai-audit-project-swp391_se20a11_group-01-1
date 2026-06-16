# Data Dictionary

## 1. Chuẩn hóa chung (Standardization)
* **ID Strategy**: Dùng `BIGINT` tự tăng (Identity) cho Primary Key của tất cả các bảng.
* **Money Strategy**: Dùng `DECIMAL(10, 2)` hoặc `DECIMAL(12, 2)` cho mọi trường tiền tệ. KHÔNG dùng `FLOAT` hay `DOUBLE`.
* **DateTime Strategy**: Dùng `DATETIME` hoặc `TIMESTAMP`, lưu chuẩn giờ UTC.
* **Enum Strategy**: Lưu xuống database dưới dạng `VARCHAR(50)` (ví dụ "PENDING") để dễ đọc và debug, thay vì số nguyên (ordinal).
* **Soft Delete Strategy**: Dùng cột `is_deleted` (BOOLEAN) hoặc `deleted_at` (DATETIME).
* **Audit Strategy**: Mọi bảng đều phải có các cột: `created_at`, `updated_at`, `created_by`, `updated_by`.

## 2. Business Status Definition (Enum Catalog)

### OrderStatus
* `PENDING`: Mới tạo, chưa được xác nhận.
* `PENDING_PAYMENT`: Đã chốt món, chờ thanh toán.
* `CONFIRMED`: Đã xác nhận/thanh toán, gửi xuống bếp.
* `PREPARING`: Bếp đang nấu.
* `READY`: Bếp đã nấu xong.
* `COMPLETED`: Đã phục vụ khách xong.
* `CANCELLED`: Đơn bị hủy.
* **Transitions**: PENDING -> CONFIRMED -> PREPARING -> READY -> COMPLETED.

### PaymentStatus
* `PENDING`: Đang chờ thanh toán.
* `PAID`: Đã thanh toán thành công.
* `FAILED`: Thanh toán lỗi.
* `REFUNDED`: Đã hoàn tiền.
* **Transitions**: PENDING -> PAID.

### ReservationStatus
* `PENDING`: Khách đặt, chờ Staff xác nhận.
* `CONFIRMED`: Staff đã xác nhận, giữ bàn.
* `SEATED`: Khách đã đến và ngồi vào bàn.
* `CANCELLED`: Khách/nhà hàng hủy.
* `COMPLETED`: Khách ăn xong và rời đi.

### TableStatus
* `AVAILABLE`: Trống.
* `RESERVED`: Đã được đặt trước.
* `OCCUPIED`: Đang có khách ngồi.
* `OUT_OF_SERVICE`: Đang bảo trì/dọn dẹp.

### EmployeeRole
* Gộp chung vào bảng Role hệ thống, cụ thể `ADMIN`, `STAFF`, `KITCHEN`.

### CouponType
* `PERCENTAGE`: Giảm theo phần trăm.
* `FIXED_AMOUNT`: Giảm số tiền cố định.

### CouponStatus
* `ACTIVE`: Đang dùng được.
* `INACTIVE`: Bị tạm khóa.
* `EXPIRED`: Hết hạn (logic tự tính từ end_date, có thể không cần enum nếu dư thừa).

## 3. Data Dictionary

### User
* **Business Description**: Tài khoản đăng nhập hệ thống.
* **Attributes**: `id`, `username`, `password_hash`, `email`, `is_active`.
* **Data Type**: `id` (BIGINT), `username` (VARCHAR 50), `password_hash` (VARCHAR 255), `email` (VARCHAR 100), `is_active` (BOOLEAN).
* **Validation**: Email phải đúng format, username unique.

### RefreshToken
* **Business Description**: Token để duy trì phiên đăng nhập và làm mới access token.
* **Attributes**: `id`, `user_id`, `token_hash`, `issued_at`, `expires_at`, `revoked_at`, `device_info`, `ip_address`, `user_agent`, `created_at`, `created_by`, `updated_at`, `updated_by`, `deleted_at`, `deleted_by`, `version`.
* **Validation**: `user_id` required. `token_hash` required, unique. `expires_at` required. `revoked_at` nullable. `expires_at` > `issued_at`.
* **Relationship**: User 1:N RefreshToken.
* **Index đề xuất**: `uk_refresh_token_hash`, `idx_refresh_token_user`, `idx_refresh_token_expires_at`.

### Role
* **Business Description**: Vai trò phân quyền.
* **Attributes**: `id`, `name`, `description`.
* **Example Values**: `ROLE_ADMIN`, `ROLE_CUSTOMER`.

### UserRole
* **Business Description**: Bảng N-N nối User và Role.
* **Attributes**: `user_id`, `role_id`.

### CustomerProfile
* **Business Description**: Hồ sơ chi tiết của khách hàng.
* **Attributes**: `id`, `user_id`, `full_name`, `phone`, `address`.

### Employee
* **Business Description**: Hồ sơ nhân sự.
* **Attributes**: `id`, `user_id`, `full_name`, `phone`, `hire_date`.

### FoodCategory
* **Business Description**: Danh mục thực đơn.
* **Attributes**: `id`, `name`, `description`, `image_url`.

### FoodItem
* **Business Description**: Món ăn/đồ uống.
* **Attributes**: `id`, `category_id`, `name`, `description`, `price`, `image_url`, `is_available`.
* **Data Type**: `price` (DECIMAL 10,2).
* **Validation**: `price` >= 0.

### Cart & CartItem
* **Business Description**: Giỏ hàng tạm.
* **Attributes (Cart)**: `id`, `user_id`, `session_id`.
* **Attributes (CartItem)**: `id`, `cart_id`, `food_item_id`, `quantity`, `unit_price`.
* **Validation**: `quantity` > 0.

### Coupon
* **Business Description**: Mã giảm giá.
* **Attributes**: `id`, `code`, `name`, `description`, `discount_type`, `discount_value`, `min_order_value`, `max_discount_amount`, `start_date`, `end_date`, `usage_limit`.
* **Example Values**: `code`: "SUMMER50", `discount_type`: "PERCENTAGE", `discount_value`: 50.

### CouponUsage
* **Business Description**: Lịch sử dùng mã giảm giá.
* **Attributes**: `id`, `coupon_id`, `user_id`, `order_id`, `used_at`.

### RestaurantTable
* **Business Description**: Bàn ăn.
* **Attributes**: `id`, `table_number`, `capacity`, `status`, `location`.
* **Example Values**: `table_number`: "T01", `capacity`: 4, `status`: "AVAILABLE".

### Reservation
* **Business Description**: Đơn đặt bàn.
* **Attributes**: `id`, `customer_id`, `table_id`, `reservation_time`, `guest_count`, `status`, `special_request`.

### Order
* **Business Description**: Đơn hàng tổng.
* **Attributes**: `id`, `customer_id`, `table_id`, `coupon_id`, `order_status`, `order_type` (DINE_IN, TAKEAWAY), `sub_total`, `discount_amount`, `total_amount`, `note`.

### OrderItem
* **Business Description**: Món ăn trong đơn.
* **Attributes**: `id`, `order_id`, `food_item_id`, `quantity`, `unit_price`, `note`.
* **Business Rule**: `unit_price` phải được copy từ bảng `FoodItem` tại thời điểm đặt.

### Payment
* **Business Description**: Giao dịch thanh toán.
* **Attributes**: `id`, `order_id`, `payment_method` (CASH, CARD, TRANSFER), `payment_status`, `amount`, `transaction_code`.

### Invoice
* **Business Description**: Hóa đơn phát hành.
* **Attributes**: `id`, `payment_id`, `invoice_number`, `tax_amount`, `total_amount`, `issued_at`.
