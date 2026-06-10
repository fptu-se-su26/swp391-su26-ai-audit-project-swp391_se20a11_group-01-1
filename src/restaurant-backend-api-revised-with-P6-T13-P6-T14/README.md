# Restaurant Backend API - Revised for SWP391 Restaurant Management System

Bản này đã được chỉnh lại để khớp với `schema.sql`, `seed.sql`, Use Case Specification và flow demo của dự án Restaurant Management System.

## API đã chỉnh

### P6-T02 Entity/Model
- Mapping đúng các bảng thật: `users`, `roles`, `permissions`, `categories`, `foods`, `food_images`, `carts`, `cart_items`, `orders`, `order_items`, `payments`, `invoices`, `restaurant_tables`.
- Dùng đúng tên cột: `user_id`, `password_hash`, `food_id`, `order_id`, `payment_status`, `invoice_number`...
- Dùng enum đúng CHECK constraint trong SQL Server.

### P6-T03 Repository/Data Access
- Repository cho các bảng chính.
- Có query theo email, role, category, cart active, order history, payment by order, invoice by order.

### P6-T04 Authentication API
```text
POST /auth/register
POST /auth/login
GET  /auth/me
```

### P6-T05 Role/Permission Logic
- Spring Security + JWT.
- Role từ seed: `CUSTOMER`, `STAFF`, `KITCHEN`, `ADMIN`.
- `/admin/**` chỉ ADMIN.
- `/staff/**` STAFF hoặc ADMIN.
- `/kitchen/**` KITCHEN hoặc ADMIN.

### P6-T06 Menu/Food API
```text
GET    /categories
GET    /foods
GET    /foods?keyword=pho
GET    /foods/{id}
GET    /foods/categories/{categoryId}
POST   /admin/foods
PUT    /admin/foods/{id}
PATCH  /admin/foods/{id}/availability
DELETE /admin/foods/{id}
```

### P6-T07 Cart API
```text
GET    /cart
POST   /cart/items
PUT    /cart/items/{itemId}
DELETE /cart/items/{itemId}
DELETE /cart
```

### P6-T08 Order API
```text
POST  /orders
GET   /orders
GET   /orders/{id}
GET   /admin/orders
GET   /admin/orders/{id}
PATCH /admin/orders/{id}/status
```

### P6-T09 Coupon Logic
```text
POST  /coupons/validate
GET   /admin/coupons
POST  /admin/coupons
PATCH /admin/coupons/{id}/status
```

### P6-T10 Payment API
```text
POST /payments
GET  /payments/{id}
GET  /payments/order/{orderId}
POST /payments/{id}/confirm
POST /payments/{id}/fail
POST /payments/{id}/refund
```

### P6-T11 Invoice API
```text
POST /invoices/orders/{orderId}/generate
GET  /invoices/{id}
GET  /invoices/orders/{orderId}
GET  /invoices/orders/{orderId}/pdf
```

## Business rule quan trọng

Invoice chỉ được tạo khi order đã có payment và `payment_status = PAID`.

Flow đúng:
```text
Cart -> Order(PENDING_PAYMENT) -> Payment(PENDING) -> Confirm Payment(PAID) -> Invoice
```

## Cách chạy

1. Mở SQL Server Management Studio.
2. Chạy `schema.sql`.
3. Chạy `seed.sql`.
4. Cấu hình DB trong `src/main/resources/application.properties`.
5. Chạy project:
```bash
mvn spring-boot:run
```

## Lưu ý demo password

Nếu `seed.sql` đang lưu password dạng `DEMO_HASH_*` thì Spring Security BCrypt sẽ không login được. Có 2 cách:

1. Đăng ký tài khoản mới qua `POST /auth/register` để hệ thống tự hash password BCrypt.
2. Hoặc cập nhật seed password bằng BCrypt hash thật.

Khuyến nghị demo: dùng `POST /auth/register` tạo customer mới, còn admin/staff/kitchen nên cập nhật password hash thật trước khi demo phân quyền.


## Added P6-T13 Kitchen API

```text
GET /kitchen/orders
GET /kitchen/orders/{orderId}
GET /kitchen/order-items?status=PENDING
PATCH /kitchen/order-items/{orderItemId}/status
POST /kitchen/order-items/{orderItemId}/accept
POST /kitchen/order-items/{orderItemId}/reject
```

## Added P6-T14 Admin/Report API

```text
GET /admin/users
PUT /admin/users/{userId}
PUT /admin/users/{userId}/roles
GET /admin/reports/dashboard
GET /admin/reports/revenue
GET /admin/reports/best-selling-foods
```
