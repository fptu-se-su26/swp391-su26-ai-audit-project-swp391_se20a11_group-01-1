# P6-T14 - Admin/Report API

Mục tiêu: Admin quản lý user/menu/coupon và xem dashboard, revenue, best-selling foods.

## Endpoints mới

```text
GET   /admin/users
GET   /admin/users/{userId}
PUT   /admin/users/{userId}
PUT   /admin/users/{userId}/roles
PATCH /admin/users/{userId}/lock
PATCH /admin/users/{userId}/activate

GET   /admin/reports/dashboard
GET   /admin/reports/revenue?from=2026-06-01&to=2026-06-30
GET   /admin/reports/best-selling-foods?limit=10
```

## Endpoints admin đã có từ các task trước

```text
POST   /admin/foods
PUT    /admin/foods/{id}
PATCH  /admin/foods/{id}/availability
POST   /admin/coupons
GET    /admin/coupons
PATCH  /admin/coupons/{id}/status
```

## Business rule

- Chỉ role `ADMIN` được gọi `/admin/**`.
- Revenue chỉ tính payment có `payment_status = PAID`.
- Best-selling foods tính từ `order_items`, bỏ item `CANCELLED` và `REJECTED`.
