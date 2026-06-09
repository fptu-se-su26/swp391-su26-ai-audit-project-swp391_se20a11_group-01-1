# P6-T07 Cart API - Postman Checklist

## 1. Login customer
POST `/auth/login`

Lấy JWT token và gắn vào Authorization:
`Bearer <token>`

## 2. Xem giỏ hàng
GET `/cart`

Expected:
- Status 200
- Có `cartId`, `items`, `totalItems`, `totalAmount`

## 3. Thêm món vào giỏ
POST `/cart/items`

Body:
```json
{
  "foodItemId": 1,
  "quantity": 2
}
```

Expected:
- Status 200
- Item xuất hiện trong giỏ
- `totalItems` và `totalAmount` cập nhật

## 4. Cập nhật số lượng
PUT `/cart/items/{cartItemId}`

Body:
```json
{
  "quantity": 5
}
```

Expected:
- Status 200
- Số lượng item được cập nhật

## 5. Xóa một item
DELETE `/cart/items/{cartItemId}`

Expected:
- Status 200
- Item bị xóa khỏi giỏ

## 6. Xóa toàn bộ giỏ hàng
DELETE `/cart`

Expected:
- Status 200
- `items` rỗng
- `totalItems = 0`
- `totalAmount = 0`
