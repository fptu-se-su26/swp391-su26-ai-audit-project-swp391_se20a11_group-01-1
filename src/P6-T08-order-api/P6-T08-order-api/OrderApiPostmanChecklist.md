# P6-T08 - Order API Postman Checklist

## 1. Tạo đơn từ cart

**POST** `/orders`

Headers:

```text
Authorization: Bearer <customer_token>
Content-Type: application/json
```

Body:

```json
{
  "couponCode": "SALE10",
  "tableId": null,
  "note": "Ít cay"
}
```

Expected:

```text
HTTP 201 Created
Order được tạo
Cart được xóa
OrderStatus = PENDING
```

## 2. Xem lịch sử đơn

**GET** `/orders`

Expected:

```text
HTTP 200 OK
Trả về danh sách order của customer hiện tại
```

## 3. Xem chi tiết đơn

**GET** `/orders/{orderId}`

Expected:

```text
HTTP 200 OK
Trả về order detail + order items
```

## 4. Customer không được xem order của người khác

Expected:

```text
HTTP 403 Forbidden
```

## 5. Admin/Staff cập nhật trạng thái đơn

**PATCH** `/admin/orders/{orderId}/status`

Body:

```json
{
  "orderStatus": "CONFIRMED"
}
```

Expected:

```text
HTTP 200 OK
OrderStatus được cập nhật
```
