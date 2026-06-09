# Manual test cho P6-T05

Đây là checklist test thủ công bằng Postman thay cho unit test tự động.

## 1. Chuẩn bị tài khoản

Database nên có ít nhất 4 user:

| Email | Role |
|---|---|
| customer@test.com | CUSTOMER |
| staff@test.com | STAFF |
| kitchen@test.com | KITCHEN |
| admin@test.com | ADMIN |

## 2. Test CUSTOMER

```http
POST /auth/login
```

Sau đó dùng token gọi:

```http
GET /api/customer/demo
```

Kết quả mong đợi: `200 OK`.

Gọi tiếp:

```http
GET /api/admin/demo
```

Kết quả mong đợi: `403 Forbidden`.

## 3. Test ADMIN

Login admin, gọi:

```http
GET /api/admin/demo
GET /api/staff/demo
GET /api/kitchen/demo
GET /api/customer/demo
```

Kết quả mong đợi: đều `200 OK`.
