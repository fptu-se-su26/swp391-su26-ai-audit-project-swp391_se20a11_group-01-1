# P6-T13 - Kitchen API

Mục tiêu: Kitchen xem món cần chế biến, xem chi tiết order, nhận/từ chối món và cập nhật trạng thái món.

## Endpoints

```text
GET   /kitchen/orders
GET   /kitchen/orders?status=CONFIRMED
GET   /kitchen/orders/{orderId}
GET   /kitchen/order-items?status=PENDING
PATCH /kitchen/order-items/{orderItemId}/status
POST  /kitchen/order-items/{orderItemId}/accept
POST  /kitchen/order-items/{orderItemId}/reject
```

## Item status hợp lệ

```text
PENDING, PREPARING, READY, SERVED, CANCELLED, REJECTED
```

## Business rule

- Kitchen chỉ xử lý order đang `CONFIRMED`, `PREPARING`, `READY`.
- Không cập nhật order đã `CANCELLED` hoặc `COMPLETED`.
- Khi món chuyển `PREPARING`, order cha chuyển `PREPARING`.
- Khi tất cả item đã ready/final, order cha chuyển `READY`.
- Reject phải có lý do trong `note`.
